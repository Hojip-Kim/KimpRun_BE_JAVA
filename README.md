# 🚀 KimpRun - 암호화폐 트레이딩 플랫폼

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.0-brightgreen)
![Java](https://img.shields.io/badge/Java-17-orange)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16.4-blue)
![Redis](https://img.shields.io/badge/Redis-Latest-red)
![License](https://img.shields.io/badge/License-Private-lightgrey)

## 📋 프로젝트 개요

KimpRun은 여러 거래소(업비트, 바이낸스, 빗썸, 코인원)에서 실시간 시장 데이터를 집계하는 고성능 암호화폐 트레이딩 플랫폼입니다. 커뮤니티 기능을 제공하고, 거래소 공지사항을 관리하며, WebSocket 연결을 통해 실시간 가격 스트리밍을 제공합니다.

### 주요 기능
- 🔄 **실시간 시장 데이터**: 4개 이상의 주요 거래소에서 WebSocket 기반 스트리밍
- 📊 **차익거래 감지**: 통합 시장 데이터 처리 및 김치 프리미엄 분석
- 🔔 **거래소 공지사항 집계**: 자동화된 스크래핑 및 알림 시스템 (텔레그램 + 애플리케이션) -> 최대 공지사항 뜬 후 20초 이내 알림
- 👥 **커뮤니티 기능**: 게시판, 댓글 및 유저 추적
- 🗄️ **CMC 데이터 조회**: CoinMarketCap 코인/거래소 메타데이터 제공 (배치 처리는 별도 서비스로 분리)
- 🔐 **OAuth2 인증**: Google 소셜 로그인 지원

## 🛠 기술 스택

### 핵심 기술
- **언어**: Java 17
- **프레임워크**: Spring Boot 3.3.0
- **빌드 도구**: Gradle 8.7

### 데이터베이스 및 저장소
- **주 데이터베이스**: PostgreSQL 16.4 (트랜잭션 데이터)
- **문서 저장소**: MongoDB 5.0 (채팅 데이터)
- **캐시 및 세션**: Redis
- **ORM**: Spring Data JPA + QueryDSL (타입 안전 쿼리)

### 실시간 및 메시징
- **WebSocket**: 실시간 데이터 스트리밍을 위한 Spring WebSocket + STOMP
- **시장 데이터**: 각 거래소별 커스텀 WebSocket 클라이언트

### 인프라 및 DevOps
- **컨테이너화**: Docker & Docker Compose
- **오케스트레이션**: Helm 차트를 사용한 Kubernetes (하이브리드 아키텍처)
  - 컨트롤 플레인: AWS EC2 + NLB
  - 워커 노드: 온프레미스 서버
- **CI/CD**: GitHub Actions → DockerHub → ArgoCD (무중단 배포)
- **모니터링**: Prometheus + Grafana
- **로깅**: ELK Stack (Elasticsearch, Logstash, Kibana)

## 🏗 아키텍처

### 계층형 아키텍처
```
Controller     →     Service     →     DAO/Repository     →     Entity
    ↓                   ↓                     ↓                   ↓
REST API            비즈니스 로직           데이터 접근 계층          도메인 모델
```

### 핵심 모듈

#### 📈 Market 모듈 (`/market`)
- 4개 이상 거래소를 위한 실시간 WebSocket 클라이언트
- 시장 데이터 집계 및 처리
- 차익거래 기회 감지

#### 🏛 Exchange 모듈 (`/exchange`)
- 거래소 관리 CRUD 작업
- 자동화된 공지사항 스크래핑 시스템
- 거래소별 구현

#### 🪙 CMC 모듈 (`/cmc`)
- CoinMarketCap 코인/거래소 메타데이터 조회
- 코인 순위 및 상세 정보 제공
- 메타데이터 조회 API (배치 처리는 별도 서비스로 분리)

#### 👥 Community 모듈 (`/community`)
- 사용자 생성 콘텐츠 관리
- 성능 최적화 쿼리
- 좋아요/조회수 추적

#### 🔌 WebSocket 모듈 (`/websocket`)
- 거래소별 전용 WebSocket 클라이언트
- 실시간 가격 스트리밍 인프라
- 사용자별 데이터 피드

## 📊 실시간 데이터 흐름

```
┌─────────────┐      ┌──────────────────┐      ┌─────────────────┐      ┌──────────────┐
│   업비트      │      │                  │      │                 │      │              │
│   바이낸스     │─────▶│   WebSocket      │─────▶│     Market      │─────▶│    STOMP     │
│   빗썸        │      │    Client        │      │     Handler     │      │  Controller  │
│   코인원      │      │                  │      │                 │      │              │
└─────────────┘      └──────────────────┘      └─────────────────┘      └──────────────┘
거래소 API WebSocket     실시간 연결 수신              데이터 처리/변환/집계     클라이언트 브로드캐스트

                                                         │
                                                         ▼
                                                ┌─────────────────┐
                                                │   PostgreSQL    │
                                                │   (시장 데이터)    │
                                                └─────────────────┘
```

### 지원하는 거래소
- 🟦 **업비트**: `UpbitWebsocketClient.java`
- 🟨 **바이낸스**: `BinanceWebSocketClient.java`
- 🟪 **빗썸**: `BithumbWebsocketClient.java`
- 🟩 **코인원**: `CoinoneWebsocketClient.java`

## 📦 배치 처리

배치 처리 시스템은 별도의 서비스로 분리되었습니다.

### 주요 배치 작업
- **CMC 데이터 동기화**: CoinMarketCap 코인/거래소 메타데이터 주기적 동기화
- **공지사항 처리**: 거래소 공지사항 집계 및 알림 처리

배치 서비스는 Spring Batch 기반으로 구현되어 있으며, Redisson 분산 락과 API 속도 제한을 통해 안전한 동시성을 보장합니다.
자세한 내용은 별도 배치 서비스 저장소를 참고하세요.

## 🚢 CI/CD 파이프라인

### GitHub Actions 워크플로우
1. **단위 테스트**: 푸시 시 자동화된 테스트 실행
2. **통합 테스트**: Docker Compose를 사용한 전체 환경 테스트
3. **빌드 및 푸시**: DockerHub로 Docker 이미지 빌드
4. **배포**: Helm 차트 업데이트 → ArgoCD → Kubernetes

```yaml
  main     →     Test     →     Build     →     Push     →     Deploy
                   ↓              ↓              ↓              ↓
                단위&통합        Docker빌드      DockerHub      K8s + ArgoCD
```

## 📁 프로젝트 구조

```
src/main/java/kimp/
├── auth/                      # 인증 처리 (OAuth2, 세션)
├── cdn/                       # CDN 관리 (Cloudflare)
├── chat/                      # 채팅 기능 (MongoDB 기반)
├── cmc/                       # CoinMarketCap 메타데이터 조회 API
├── common/                    # 공유 유틸리티 (DTO, 공통 로직)
├── community/                 # 커뮤니티 기능 (게시판, 댓글, 카테고리)
├── config/                    # 애플리케이션 설정 (DB, Redis, Security, WebSocket)
├── exception/                 # 예외 처리 및 글로벌 예외 핸들러
├── exchange/                  # 거래소 관리 (CRUD, 메타데이터)
├── filter/                    # 커스텀 필터 (인증, 로깅)
├── logs/                      # 로그 관리
├── market/                    # 시장 데이터 처리 (실시간 가격, 차익거래)
├── news/                      # 뉴스 관리
├── notice/                    # 공지사항 관리 (알림, 텔레그램 연동)
├── preloader/                 # 애플리케이션 시작 시 데이터 사전 로드
├── scrap/                     # 웹 스크래핑 서비스 연동 (거래소별 공지사항 수집)
├── security/                  # 보안 설정 (SecurityConfig, 인가)
├── telegram/                  # 텔레그램 봇 연동 (공지사항 알림)
├── user/                      # 사용자 관리 (회원, 권한)
├── util/                      # 유틸리티 클래스
├── webhook/                   # 웹훅 처리 (Slack)
└── websocket/                 # WebSocket 처리 (STOMP, 실시간 스트리밍)
```

## 🧪 테스트

### 테스트 구조
- **단위 테스트**: `/src/test/java/unit/`
- **통합 테스트**: `/src/test/java/integration/`

## 📝 개발 가이드라인

### 코드 규약
- **서비스 패턴**: 인터페이스 + 구현체 분리
- **DAO 패턴**: QueryDSL을 사용한 커스텀 DAO
- **DTO 구조**: Request/Response 분리
- **패키지 구조**: 도메인 주도 설계

### 커밋 메시지 형식
```
feat: 새로운 기능 추가
fix: 버그 수정
refactor: 코드 리팩토링
docs: 문서 업데이트
test: 테스트 추가/수정
chore: 유지보수 작업
```

## 📄 라이선스

이 프로젝트는 독점 소프트웨어입니다. 모든 권리가 보유됩니다.

## 🔗 링크

- [김프런](https://kimprun.com)
