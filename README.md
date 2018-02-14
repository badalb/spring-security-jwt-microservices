# spring-security-jwt-microservices

Sample application to demonestrate how spring security jwt will work in microservices environment. 

i. auth-service will authenticate users and provide a jwt. It uses private key to generate tokens

ii. resource-service has the resurces and consumers needs to send Bearer token which will be decrypted using a 
public key.
