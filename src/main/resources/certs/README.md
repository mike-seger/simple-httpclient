# re-creating the default test client and trust cert

openssl genrsa -out mykey.pem 2048
openssl req -new -x509 -key mykey.pem -out mycert.pem -days 3650 \
    -subj "/C=US/ST=New York/L=New York City/O=My Company Name/OU=Department/CN=www.example.com"
openssl x509 -outform der -in mycert.pem -out mycert.der
keytool -import -alias mycert -file mycert.pem \
    -keystore client_truststore.p12 -storepass password -noprompt -storetype PKCS12

openssl req -new -x509 -key mykey.pem -out server_cert.cer -days 3650 -outform DER \
    -subj "/C=US/ST=New York/L=New York City/O=My Company Name/OU=Department/CN=www.example.com"
keytool -import -alias servercert -file server_cert.cer -keystore server_truststore.p12 \
    -storetype PKCS12 -storepass password -noprompt



# the client keystore
keytool -genkeypair -alias clientkey -keyalg RSA -keysize 2048 -storetype PKCS12 \
    -keystore client_keystore.p12 -validity 3650 -storepass password -keypass password \
    -dname "CN=Client, OU=Department, O=Organization, L=City, S=State, C=Country"

keytool -export -alias clientkey -keystore client_keystore.p12 \
    -file client_cert.cer -storepass password





keytool -import -alias servercert -file server_cert.cer \
    -keystore client_truststore.p12 -storetype PKCS12 -storepass password -noprompt
