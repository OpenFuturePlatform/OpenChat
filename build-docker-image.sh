VERSION=5.4
docker build --platform linux/amd64 -t iceknight07/open-chat:$VERSION .
docker push iceknight07/open-chat:$VERSION