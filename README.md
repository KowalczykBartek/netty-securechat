[![](https://img.shields.io/badge/unicorn-approved-ff69b4.svg)](https://www.youtube.com/watch?v=9auOCbH5Ns4)

## why ?

Because of configuring SSL/TLS support (and creating keystore/truststore) in Java always poses a problem (at least for me (｡◕‿‿◕｡) ), I crafted this end-to-end project (based on netty's secure-chat example project). 

If you want, you can just clone this and run, there is already jks file for client (which included self-signed-certificate) and jks file for server (this one includes cert file and its private key).

But if you want to generate your own self-signed certificate, perform following steps (you will need openssl) :

## self-signed cert generation
 
- generate self signed certificate 
	```bash
	openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem -days 365   
	```

- create jks file for client (it will include certificate)
	```bash
	$ keytool -import -v -trustcacerts -alias client-alias -file cert.pem -keystore client.jks -keypass tutorial123 -storepass tutorial123  
	```

- create jks file for server (it will need private key included)
	```bash
	openssl pkcs12 -export -in cert.pem -inkey key.pem -certfile cert.pem -out keystore.p12
	keytool -importkeystore -srckeystore keystore.p12 -srcstoretype pkcs12 -destkeystore server.jks -deststoretype JKS
	```

## other
1. This project assumes that client sends hello message (client has setUseClientMode set to "true")

2. Remember to create new instance of SslEngine for each handler in your own project ;D 

3. And this simple property "javax.net.debug" set to "true" can help you a lot !
	```bash
	System.setProperty("javax.net.debug", "all");
	```

## links
I was able to run this thanks to:             
http://xacmlinfo.org/2014/06/13/how-to-keystore-creating-jks-file-from-existing-private-key-and-certificate/
https://stackoverflow.com/questions/10175812/how-to-create-a-self-signed-certificate-with-openssl
https://security.stackexchange.com/questions/20803/how-does-ssl-tls-work

hope that will help you !