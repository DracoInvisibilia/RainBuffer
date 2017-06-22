#include <Wire.h>

// SENSOR CALLS
#define NOT_SELECTED 0
#define WATER_LEVEL 1
#define WATERFLOW_IN 2
#define WATERFLOW_SEWER 3
#define WATERFLOW_FACUET 4
#define WATERFLOW_GARDEN 5
#define TEMPERATURE 6
#define WATERGATE_SEWER 7
#define WATERGATE_GARDEN 8

// COMMANDS
#define INIT 0
#define GET 1
#define SET 2
#define RESET 3

// ERRORS
#define NO_ERROR 0
#define INVALID_COMMAND 1
#define INVALID_VALUE 2
#define COMMAND_FAILURE 3
#define SENSOR_FAILURE 4
#define OTHER_ERROR 5

// SENSOR PINS
#define WF_IN 2
#define US_TRIG 30
#define US_ECHO 31
#define VALVE_GARDEN 33
#define SLAVE_ADDRESS 0x04

#define MA_WINDOW 10
#define MAX_SLEEP 900

int number = 0;
int state = 0;

//Arduino settings (can be changed by Pi)
int ARDUINO_SLEEP = 500;

//Input from Pi things
int32_t buffer[255];
byte received_packet[6];

//Packet thingies
int8_t pkt_sid;
int8_t pkt_command;
int32_t pkt_value;
int32_t pkt_error;

//Transmitting thingies
int32_t toSend = -1;
byte transmit_packet[10];

//Sensor stuff
int32_t WF_in_pulses = 0;
long US_time, US_dist;
int32_t MA_BUF[MA_WINDOW];
int32_t MA_POS = 0;
bool MA_INIT = true;
int valveState = LOW;

volatile uint8_t lastflowpinstate;

long update_MA(long new_val) {
  MA_BUF[MA_POS] = new_val;
  if(MA_POS+1==MA_WINDOW) {
    MA_POS = 0;
  } else {
    MA_POS++;
  }
  if(MA_INIT && MA_POS==MA_WINDOW-1) MA_INIT=false;
  int32_t sum = 0;
  for(int i = 0; i < MA_WINDOW; i++) {
    sum += MA_BUF[i];
  }
  if (MA_INIT) {
    return (sum/(MA_POS+1));
  } else {
    return (sum/MA_WINDOW);
  }
}

void useInterrupt(boolean v) {
  if (v) {
    // Timer0 is already used for millis() - we'll just interrupt somewhere
    // in the middle and call the "Compare A" function above
    OCR0A = 0xAF;
    TIMSK0 |= _BV(OCIE0A);
  } else {
    // do not call the interrupt function COMPA anymore
    TIMSK0 &= ~_BV(OCIE0A);
  }
}

SIGNAL(TIMER0_COMPA_vect) {
  uint8_t x = digitalRead(WATERFLOW_IN);
  if (x == lastflowpinstate) {
    return; // nothing changed!
  }
  if (x == HIGH) {
    WF_in_pulses++;
  }
  lastflowpinstate = x;
}
 
void setup() {
  Serial.begin(9600);
  Serial.print("Setting up!\n");
  pinMode(13, OUTPUT);
 
  // initialize i2c as slave
  Wire.begin(SLAVE_ADDRESS);
 
  // define callbacks for i2c communication
  Wire.onReceive(receiveData);
  Wire.onRequest(sendData);

  pinMode(WATERFLOW_IN, INPUT);
  pinMode(US_TRIG, OUTPUT);
  pinMode(US_ECHO, INPUT);
  pinMode(VALVE_GARDEN, OUTPUT);

  digitalWrite(WATERFLOW_IN, HIGH);
  digitalWrite(VALVE_GARDEN, LOW);
  lastflowpinstate = digitalRead(WATERFLOW_IN);
  useInterrupt(true);
}
 
void loop() {
  digitalWrite(US_TRIG, LOW);
  delayMicroseconds(2);
  digitalWrite(US_TRIG, HIGH);
  delayMicroseconds(10);
  digitalWrite(US_TRIG, LOW);

  US_time = pulseIn(US_ECHO, HIGH);
  US_dist = (US_time/2) / 29.1; 
  US_dist = update_MA(US_dist);
  
  Serial.print("Distance: "); Serial.println(US_dist, DEC);
  float liters = WF_in_pulses;
  liters /= 7.5;
  liters /= 60;
  liters *= 1000;
  Serial.print("Liters: "); Serial.println(liters, DEC);
 

  /* 
  digitalWrite(VALVE_GARDEN, LOW);
  Serial.println("Set to low");
  delay(2000);
  digitalWrite(VALVE_GARDEN, HIGH);
  Serial.println("Set to high");
  delay(2000);
  */
  delay(ARDUINO_SLEEP);
}
 
// callback for received data
void receiveData(int byteCount){
   int count = 0;
   while(Wire.available()) {
    //number = Wire.read();
    received_packet[count] = Wire.read();
    //Serial.print("Byte: "); Serial.println(received_packet[count]);
    count++;
  }

  pkt_sid = received_packet[0];
  pkt_command = received_packet[1];
  pkt_value = received_packet[2] << 24 | (received_packet[3] & 0xFF) << 16 | (received_packet[4] & 0xFF) << 8 | (received_packet[5] & 0xFF);
  Serial.print("sensor_id: "); Serial.println(pkt_sid);
  Serial.print("command: "); Serial.println(pkt_command);
  Serial.print("value: "); Serial.println(pkt_value);
  
    //Serial.print("buffer-value: "); Serial.println(buffer[i]);
    float liters = 0;
    pkt_error = 0;
    switch(pkt_sid) {
      
      case NOT_SELECTED:
        switch(pkt_command) {
          case INIT:
            pkt_error=NO_ERROR;
            break;
          case SET:
            if(pkt_value<0 || pkt_value>MAX_SLEEP) {
              pkt_error = INVALID_VALUE;
            } else {
              ARDUINO_SLEEP = pkt_value;
              if(ARDUINO_SLEEP!=pkt_value) pkt_error = COMMAND_FAILURE;
            }
            break;
          case RESET:
            ARDUINO_SLEEP = 500;
            if(ARDUINO_SLEEP!=500) pkt_error = COMMAND_FAILURE;
            break; 
          default:
            pkt_error=INVALID_COMMAND;
            break;
        }
        break;
        
      case WATERFLOW_IN:
        switch(pkt_command) {
          case GET:
            Serial.print("Waterflow_in requested. Value: "); Serial.println(WF_in_pulses);
            liters = WF_in_pulses;
            liters /= 7.5;
            liters /= 60;
            liters *= 1000;
            pkt_value = ((int) liters);
            if(liters<0 || liters>999) pkt_error = SENSOR_FAILURE;
            break;
          case SET:
            if(pkt_value>999 || pkt_value<0) {
              pkt_error = INVALID_VALUE;
            } else {
              WF_in_pulses = pkt_value/1000;
              WF_in_pulses *= 60;
              WF_in_pulses *= 7.5;
            }
            break;
          case RESET:
            WF_in_pulses = 0;
            if(WF_in_pulses!=0) pkt_error = COMMAND_FAILURE;
            break;
          default:
            pkt_error = INVALID_COMMAND;
            break;
        }
        break;
        
      case WATER_LEVEL:
          switch(pkt_command) {
            case GET:
              Serial.print("Ultrasonic waterlevel requested. VAL="); Serial.println(US_dist);
              pkt_value = ((int)US_dist);
              if(US_dist==0 || US_dist > 500) pkt_error = SENSOR_FAILURE;
              break;
            case SET:
              if(pkt_value > 1000 || pkt_value<0) {
                pkt_error = INVALID_VALUE;
              } else {
                while (count < MA_WINDOW) {
                  update_MA(pkt_value);
                  count++;
                }
                if((int)US_dist!=pkt_value) pkt_error = COMMAND_FAILURE;
              }
              break;
            case RESET:
              while (count < MA_WINDOW) {
                update_MA(0);
                count++;
              }
              if((int)US_dist!=0) pkt_error = COMMAND_FAILURE;
              break;
            default:
              pkt_error = 1;
              break;
            }
          break;

      case WATERGATE_SEWER:
        switch(pkt_command) {
          case SET:
            if(pkt_value==1) {
              valveState==HIGH;
              digitalWrite(VALVE_GARDEN, HIGH);
            } else if (pkt_value==0) {
              valveState==LOW;
              digitalWrite(VALVE_GARDEN, LOW);
            } else {
              pkt_error = INVALID_VALUE;
            }
            break;
          case RESET:
            valveState==LOW;
            digitalWrite(VALVE_GARDEN, LOW);
            break;
          default:
            pkt_error = INVALID_COMMAND;
            break;
          }
          break;

          
      default: 
        pkt_error = SENSOR_FAILURE;
        break;
      }
}
 
// callback for sending data
void sendData() {
 byte sendPacket[10];
 sendPacket[0] = pkt_sid;
 sendPacket[1] = pkt_command;
 sendPacket[2] = (pkt_value >> 24) & 0xFF;
 sendPacket[3] = (pkt_value >> 16) & 0xFF;
 sendPacket[4] = (pkt_value >> 8) & 0xFF;
 sendPacket[5] = pkt_value & 0xFF;
 sendPacket[6] = (pkt_error >> 24) & 0xFF;
 sendPacket[7] = (pkt_error >> 16) & 0xFF;
 sendPacket[8] = (pkt_error >> 8) & 0xFF;
 sendPacket[9] = pkt_error & 0xFF;
 Wire.write(sendPacket,10);
 Serial.print("SEND_PACKET: pkt_sid: "); Serial.print(pkt_sid);
 Serial.print("| pkt_command: "); Serial.print(pkt_command);
 Serial.print("| pkt_value: "); Serial.print(pkt_value);
 Serial.print("| pkt_error: "); Serial.println(pkt_error);
}
