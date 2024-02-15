# BE 서버 실행 방법

1. gradle 설치

```bash
brew install gradle
```

2. BE 서버 클론(한번 한 이후엔 pull)
3. clean / build - 새로 pull 한 이후(서버의 변경사항을 반영하고 싶을때에는) pull, clean, build 를 반드시 해주어야 합니다.

```bash
// BE 서버 디렉토리로 이동 후
./gradlew clean
./gradlew :togather-web:build
```

4. 빌드된 파일 실행

```bash
java -jar -Djasypt.encryption.key={}  ./togather-web/build/libs/togather-web.jar
```

5. 브라우저 [localhost:8080](http://localhost:8080) 으로 접속 후 다음 확인
    1. [localhost:8080/hello](http://localhost:8080/hello) → 웹페이지에 hello 나옴
    2. [localhost:8080/hello/1](http://localhost:8080/hello/1) → 제 이름 나옴
    3. [localhost:8080/hello/2](http://localhost:8080/hello/2) → 유저를 찾을 수 없다고 나옴
6. 백엔드의 업데이트 없이 반복적으로 서버만 껐다 켰다 하고싶을땐 clear/build 필요 없이 4번에 알려드린 실행 명령어만 치시면 됩니다.
