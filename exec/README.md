# 1. gitlab 소스 클론 이후 빌드 및 배포할 수 있는 작업문서

1. 사용한 JVM, 웹서버 버전, 설정
    - JVM : 1.8버전
    - 웹서버 : 스프링부트 2.4.5
    - 서비스 포트번호 : 443
    - IDE

        Intellij 2021.1.3

        vscode 1.59.0

2. 빌드 시 사용되는 환경 변수 등의 주요 내용 상세 기재

    applicaton.properties 파일

3. 배포 시 특이사항 기재
    - 서버는 letsencrypt를 사용하여 ssl 설정
    - `letsencrypt certonly --standalone -d [도메인명]` 명령어로 얻은 keyfile을 ~/apps/narang/certificates/live/[도메인명] 에 복사
    1. 로컬 프로젝트의 back 폴더에서 터미널 실행하여 gradle clean build 명령어 실행
    2. aws ubuntu 접속
    3. ~/apps/narang/libs 에 jar 파일 저장
    4. ~/apps/narang에 docker-compose.yml 파일과 .env 파일 작성
    5. ~/apps/narang/db 에 init.sql 파일 작성
    6. `docker-compose up -d` 명령어로 컨테이너 다중 실행
4. 데이터베이스 접속 정보 등 프로젝트에 활용되는 주요 계정 및 프로퍼티가 정의된 파일 목록
    - narang.p12

        spring boot 프로젝트의 src\main\resources/keystore에 넣을 keyfile

    - init.sql

        db 초기화 파일

    - .env

        openvidu 환경변수 파일

    - Dockerfile

        jar 파일을 컨테이너로 실행할 때 필요한 설정 파일

    - docker-compose.yml

        도커 다중 컨테이너 실행을 위한 설정 파일

# 2. 프로젝트에서 사용하는 외부 서비스 정보 문서

이미지파일 저장을 위해 imgbb 저장소 사용

[https://api.imgbb.com/](https://api.imgbb.com/)

# 3. DB 덤프 파일

narang01dump.sql
