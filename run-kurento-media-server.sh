docker run -d \
    -v /tmp/recordings:/tmp/recordings \
    -p 8888:8888/tcp \
    -p 5000-5050:5000-5050/udp \
    -e KMS_MIN_PORT=5000 \
    -e KMS_MAX_PORT=5050 \
    -e KMS_STUN_IP=stun.l.google.com \
    -e KMS_STUN_PORT=19302 \
    -e KMS_TURN_URL=2541d7b4503e7956a66ab39e:fInHfabub1gAFe74@188.245.177.56:80 \
    kurento/kurento-media-server:7.1.0