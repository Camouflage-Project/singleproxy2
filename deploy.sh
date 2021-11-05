(cd frontend; yarn run build) && \
rm -r ./src/main/resources/frontend/*; \
mv ./frontend/build/* \
./src/main/resources/frontend/ && \
./gradlew clean shadowJar && \
scp ./build/libs/singleproxy2-0.0.1-all.jar root@116.203.232.229:/root/camouflage