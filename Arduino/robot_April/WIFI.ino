int status = WL_IDLE_STATUS;     // the Wifi radio's status
WiFiUDP Udpread;

//unsigned int readPort = 2390;      // puerto local para leer datos
unsigned int readPort = 55056; 
char packetBuffer[255]; //buffer to hold incoming packet

void ini_WIFI_WPA(char *c_ssid, char *c_pass){  
  // check for the presence of the shield:
  if (WiFi.status() == WL_NO_SHIELD) {
    Serial.println("WiFi shield no esta conectada");
    // don't continue:
    while (true);
  }
  while ( status != WL_CONNECTED) {
    Serial.print("Intentando conectar con la WPA SSID: ");
    Serial.println(c_ssid);
    // Connect to WPA/WPA2 network:
    status = WiFi.begin(c_ssid, c_pass);
    // wait 10 seconds for connection:
    delay(10000);
  }
  Udpread.begin(readPort); //inicializamos la lectura de tramas UDP en el puerto
  printCurrentNet(); //Visualizamos la SSID, BSSID...
  printWifiData(); // visualizamos los datos de la shield wifi (IP,MAC)
}

struct Tramas LeeTrama(void) //Leemos el puerto y retornamos una estructura con los datos del paquete recibido
{  
  char *recibidos;
  struct Tramas Paquete1;
  recibidos=EscuchaPuerto(); //Leemos la trama recibida en formato char.
  if(recibidos[0]!='\0'){ 
     Paquete1.numero=recibidos[0];
     Paquete1.cabecera=recibidos[1];
     for(byte i=0;i<8;i++){
       Paquete1.datos[i]=recibidos[i+2];
     }
     Paquete1.activa=true;        
  }else{Paquete1.activa=false;}  
  
  //Serial.print("Cabecera:");
  //Serial.println(Paquete1.cabecera);
  //Serial.print("Datos:");
  //Serial.println(Paquete1.datos[1]);
  return Paquete1;
}

char* readWifiProtocol(void)
{
  char *recibidos;
  recibidos=EscuchaPuerto();
    
    //Starting the reading protocol
    //----
    
  return recibidos;
}


char *EscuchaPuerto(){  // Escuchamos el puerto y devolvemos toda la trama en formato char.
  // if there's data available, read a packet
  int packetSize = Udpread.parsePacket();
  if (packetSize)
  {
    // read the packet into packetBufffer
    int len = Udpread.read(packetBuffer, 255);
    if (len > 0) packetBuffer[len] = '\0'; //Put a line ending
    
    Serial.print("PacketBuffer: ");
    Serial.println(packetBuffer);
    
  }else{
    packetBuffer[0]='\0';
  }  
 return packetBuffer;
}

char *readUDP(void){  // Escuchamos el puerto y devolvemos toda la trama en formato char.
  // if there's data available, read a packet
  int packetSize = Udpread.parsePacket();
  if (packetSize)
  {
    // read the packet into packetBufffer
    int len = Udpread.read(packetBuffer, 255);
    if (len > 0) {
      packetBuffer[len] = '\0'; //Put a line ending
      Serial.print("Lenght Detected:");
      Serial.println(len);
    }
    //Serial.print("PacketBuffer: ");
    //Serial.println(packetBuffer);
    return packetBuffer;
  }else{
    //packetBuffer[0]='\0';
    return "E"; //No data
  }  
}


void sendControlUDP(){
  //Data to send: temp,LEDs,colision,manual/auto,speed,proximity
  Udpread.beginPacket("192.168.1.34", 4560); //Android Jordi Casa
  char dataTX[10];
  
  int temp = (int)Lee_temperatura();//Casting from float
  int LEDs = updateLEDs();
  char colision = 'N';    //char colision = getColisionStatus();
  char manualAuto = 'M';  //char manualAuto = getManualAutoStatus();
  int speedValue = 2;     //int speedValue = getSpeedValue();
  char proximity = 'N';   //char proximity = getProximityStatus();
  
  //Starting TX protocol
  dataTX[0]='C'; //Data Type Control
  String str = String(temp);
  char b;
  //char tempchar[3] = itoa(temp);
  //dataTX[1]=tempchar[0];
  //dataTX[2]=tempchar[1];
  //dataTX[3]=tempchar[2];
  dataTX[1]=str[0];
  dataTX[2]=str[1];
  dataTX[3]=str[2];
  
  str = String(LEDs); 
  //dataTX[4]=str.toCharArray(b,1);
  dataTX[4]=str[0];
  dataTX[5]=colision;
  dataTX[6]=manualAuto;
  str = String(speedValue);
  dataTX[7]=str[0];
  dataTX[8]=proximity;
  dataTX[9]='\0';//End
  
  Serial.print("Data Sent: ");
  Serial.println(dataTX);
  
  Udpread.write(dataTX);
  Udpread.endPacket(); 
}

void sendLaberynthUDP(){
  //Data to send: position,path,wallsFound,solution
}

void sendAccelUDP(){
  //Data to send: X,Y,Z
}


void EscribePuerto(char *Trama){  // enviamos una trama char al puerto para enviarlo al destino
  //Udpread.beginPacket(Udpread.remoteIP(), Udpread.remotePort());
  Udpread.beginPacket("192.168.1.34", 4560); //Android Jordi Casa
  Udpread.write(Trama);
  Udpread.endPacket();  
}

void printWifiData() {
  // print your WiFi shield's IP address:
  IPAddress ip = WiFi.localIP();
  Serial.print("IP Address: ");
  Serial.println(ip);
  Serial.println(ip);

  // print your MAC address:
  byte mac[6];
  WiFi.macAddress(mac);
  Serial.print("MAC address: ");
  Serial.print(mac[5], HEX);
  Serial.print(":");
  Serial.print(mac[4], HEX);
  Serial.print(":");
  Serial.print(mac[3], HEX);
  Serial.print(":");
  Serial.print(mac[2], HEX);
  Serial.print(":");
  Serial.print(mac[1], HEX);
  Serial.print(":");
  Serial.println(mac[0], HEX);

}

void printCurrentNet() {
  // print the SSID of the network you're attached to:
  Serial.print("SSID: ");
  Serial.println(WiFi.SSID());

  // print the MAC address of the router you're attached to:
  byte bssid[6];
  WiFi.BSSID(bssid);
  Serial.print("BSSID: ");
  Serial.print(bssid[5], HEX);
  Serial.print(":");
  Serial.print(bssid[4], HEX);
  Serial.print(":");
  Serial.print(bssid[3], HEX);
  Serial.print(":");
  Serial.print(bssid[2], HEX);
  Serial.print(":");
  Serial.print(bssid[1], HEX);
  Serial.print(":");
  Serial.println(bssid[0], HEX);

  // print the received signal strength:
  long rssi = WiFi.RSSI();
  Serial.print("signal strength (RSSI):");
  Serial.println(rssi);

  // print the encryption type:
  byte encryption = WiFi.encryptionType();
  Serial.print("Encryption Type:");
  Serial.println(encryption, HEX);
  Serial.println();
}

