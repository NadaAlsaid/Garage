#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include <Servo.h>
#include <FirebaseESP8266.h>
#include <Adafruit_Sensor.h>
#include <DHT.h>
#include <DHT_U.h>
#include <NTPClient.h>
#include <WiFiUdp.h>

/* Put your SSID & Password */
const char* ssid = "";   //Enter SSID here
const char* password = "";  //Enter Password here
const int wakeUpInterval = 10 * 1000000ul;  
#define FIREBASE_HOST "https://garage-37f62-default-rtdb.firebaseio.com"
#define FIREBASE_AUTH "WJC80FQ2CUvopfv5MVDGwp6CKDWKxXJqQNo63XDM"
#define DHTTYPE    DHT11
#define DHTPIN D2
Servo myServo ;
ESP8266WebServer server(80);

//left -> right
int trigParkingArea[2] = {D5 , D1} ;
int echoParkingArea[2] = {D6 , D8} ;
int ledParkingArea[2] = {D3 , D4} ;

//controls

int numberOfCars = 0 ;
bool flag = false , flagServo = false , hereOrOut = 0 ;

int tmp = 10  , cnt = 0;
float dis = 0 , tempReading = 0 ;
unsigned int timeParking = 0 , timeOpeningServo = 0 , timeSendTempReading = 0 ;
int price[2] = { 0 , 0 }, go_out[2] = { 0 , 0 } ;
String email[2] , is_available[2] , s , pass , priceInString ;
String Door_status  = "C" ;
FirebaseData firebaseData;
DHT_Unified dht(DHTPIN, DHTTYPE);

void setup() {
  connectWiFi();
  for (int i = 0 ; i < 2 ; i++) {
    pinMode(trigParkingArea[i] , OUTPUT );
    pinMode(echoParkingArea[i] , INPUT );
    pinMode(ledParkingArea[i] , OUTPUT );
  }

  //  timeParking3 = millis() ;
  timeOpeningServo = millis() ;
  timeParking  = millis() ;
  timeSendTempReading = millis() ;
  myServo.attach(D7);
  myServo.write(50);
  dht.begin();
  Firebase.begin( FIREBASE_HOST, FIREBASE_AUTH );
//  for (int i = 0 ; i < 2 ; i++) {
//    s  = "Spots/spot" + String(i + 1) + "/email" ;
//    Firebase.getString(firebaseData, s, email[i]) ;
//    delay(10);
//    if (email[i] != "") {
//      email[i].replace(".com" , "");
//      s =  "subscription/" + email[i] + "/price" ;
//
//      Firebase.setString(firebaseData, s, priceInString);
//      if(priceInString != " "){
//        price[i] = priceInString.toInt();
//        go_out[i] = 1 ;
//      }
//    }
//  }

}
void loop() {
  //  FirebaseObject object = Firebase.get("/");

  numberOfCars = 0 ;
  if (WiFi.status() == WL_CONNECTED) {
    if (millis() - timeParking  >= 5000 ) {

      for (int i = 0 ; i < 2 ; i++) {

         s  = "Spots/spot" + String(i + 1) + "/email" ;
         Firebase.getString(firebaseData, s , email[i]);
        delay(10);
        s = "Spots/spot" + String(i + 1) + "/is_available";
        Firebase.getString(firebaseData, s , is_available[i]);

        /* Serial.println(" Uploaded Successfully" + is_available[i]);
          Serial.println( flag );
          Serial.println( price[i]);*/

        Serial.println( "go_out[i]"  );
        Serial.println( go_out[i]  );
        if (go_out[i] >= 2 ) {
          price[i] = 0 ;
          s  = "Spots/spot" + String(i + 1) + "/email" ;
          Firebase.setString(firebaseData, s, "") ;
          delay(10);
          s = "Spots/spot" + String(i + 1) + "/is_available";
          if (Firebase.setString(firebaseData, s, "true" )) {
            delay(10);
            s = "Spots/spot" + String(i + 1) + "/password";
            Firebase.setString(firebaseData, s, "false" );
            delay(10);
            /*Serial.print("data ");
              Serial.print("'");
              Serial.print("true");
              Serial.print("'");
              Serial.println("if Uploaded Successfully");*/
          }
 
          email[i].replace(".com" , "") ;
          if (Firebase.setString(firebaseData, "subscription/" + email[i] + "/price", " " )) {
            delay(10);
            /* Serial.print("data ");
              Serial.print("'");
              Serial.print("0");
              Serial.print("'");
              Serial.println("if Uploaded Successfully");*/

          }

          go_out[i] = 0 ;
        } else if (is_available[i].startsWith("false")) {
          s  = "Spots/spot" + String(i + 1) + "/email" ;
          Firebase.getString(firebaseData, s , email[i]);
          email[i].replace(".com" , "");
          s = "subscription/" + email[i] + "/spot_id" ;
          Firebase.setString(firebaseData, s , String(i + 1));
          delay(10);
          price[i] += 3 ;
          s =  "subscription/" + email[i] + "/price" ;

          if (Firebase.setString(firebaseData, s, String(price[i]))) {
            delay(10);
            Serial.println("email[i]  "  + email[i]);
            Serial.println("i  "  + i );
            Serial.print("data ");
            Serial.print("'");
            Serial.print(price[i]);
            Serial.print("'");
            Serial.println("else Uploaded Successfully");

          }
        }


      }
      timeParking = millis() ;
    }
    for (int i = 0 ; i < 2 ; i++) {
      delay(100);
      flag = parking_Area( trigParkingArea[i] , echoParkingArea[i] , ledParkingArea[i] ) ;
      
      Firebase.getString(firebaseData, "Door_status", Door_status);

      while (!Door_status.startsWith("open") && ! Door_status.startsWith("Close")) {
        Firebase.getString(firebaseData, "Door_status", Door_status);
        delay(100);
        Serial.println("Door_status");
        Serial.println(Door_status);
      }
      delay(10);
      s = "Spots/spot" + String(i + 1) + "/password";
      Firebase.getString(firebaseData, s , pass);

      while (!pass.startsWith( "true" ) && !pass.startsWith( "false" )) {
        s = "Spots/spot" + String(i + 1) + "/password";
        delay(100);
        Firebase.getString(firebaseData, s , pass);
        Serial.println("pass");
        Serial.println(pass);
      }
      if ( Door_status.startsWith("open") &&  pass.startsWith( "true" ) ) {
        //          s  = "Spots/spot" + String(i + 1) + "/email" ;
        //          Firebase.setString(firebaseData, s, "") ;
        //         s = "Spots/spot" + String(i + 1) + "/is_available";
        //         Firebase.setString(firebaseData, s, "true" );
        myServo.write(0);
        //        timeOpeningServo = millis();
        Serial.println("open");
        go_out[i]++;
        //        s = "Spots/spot" + String(i +1) + "/password";
        //        Serial.println(s);
        //        if(Firebase.setString(firebaseData, s , "false")){
        Serial.println("close");
        Firebase.setString(firebaseData, "Door_status" , "Close" );
        delay(1000);
        myServo.write(50);
        //        }
      }
    }


    if (millis() - timeSendTempReading  >= 5000 ) {
      sensors_event_t event;
      dht.temperature().getEvent(&event);
      if (isnan(event.temperature)) {
        Serial.println(F("Error reading temperature!"));
      }
      else {
        Firebase.setString(firebaseData, "Temperature", String(event.temperature) );
        Serial.print(F("Temperature: "));
        Serial.print(event.temperature);
        Serial.println(F("°C"));
      }


      timeSendTempReading = millis() ;
    }

  } else {
//    digitalWrite(ledParkingArea[0], LOW) ;
//    digitalWrite(ledParkingArea[1]  , LOW) ;
    Serial.println("No Connection");
    ESP.deepSleep(wakeUpInterval, WAKE_RF_DEFAULT);

  }


}

bool parking_Area(int trig , int echo , int ledPin ) {
  digitalWrite(trig, LOW);
  delayMicroseconds(2);
  digitalWrite(trig, HIGH) ;
  delayMicroseconds(5);
  digitalWrite(trig, LOW);
  delayMicroseconds(2);
  float dur = pulseIn(echo , HIGH) ;
  dis = dur / 29 / 2 ;
  Serial.println(dis);
  hereOrOut = flag ;
  if (dis > 20) {
    digitalWrite(ledPin, HIGH);

    return true ;
  }
  else {
    digitalWrite(ledPin, LOW) ;

    return false ;
  }

}


void connectWiFi()
{
  Serial.begin(115200);
  while (!Serial);
  Serial.println();
  Serial.println("ESP8266 has waked up.");
  delay(500);
  WiFi.persistent(false);     // <-- prevents flash wearing?
  WiFi.forceSleepBegin();
  delay(1);
  WiFi.forceSleepWake();
  delay(1);
  Serial.println("Connecting to WiFi..." );
  //  Serial.println( ssid );
  //  Serial.println(password);
  WiFi.begin(ssid, password);
  int cnt = 0 ;
  while (WiFi.status() != WL_CONNECTED && cnt != 30)
  {
    delay(500);
    Serial.print(".");
    cnt++ ;
  }
  Serial.println("");
  if (WiFi.status() == WL_CONNECTED)
    Serial.println("Connected to WiFi!");
}
