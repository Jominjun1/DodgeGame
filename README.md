탄막 피하기 게임 ( 멀티 클라이언트 )

개인 or 2인 이상 게임 참여 가능

서버에 멀티 클라이언트가 접속하여 게임 시작/게임 방법/게임 종료 버튼을 통하여 상호작용 가능. ( Server.java , Client.java , Server.java(ClientHandler.java) )

게임 시작 버튼을 누르면 게임 화면을 보여주고 아이템, 탄막 , 플레이어를 멀티 버퍼링을 사용해서 조금 더 부드러운 프레임을 보여준다 ( GameController.java )

게임 시작시 중앙에서 탄막이 생성되며 탄막은 2초마다 또 생성됨, 생성된 탄막은 벽에 충돌하면 랜덤 방향으로 다시 진행 ( bullet.java , bulletManager.java )

아이템이 5초마다 랜덤으로 생성, 아이템은 총 6가지의 동일한 확률의 랜덤 효과를 플레이어에게 부여. (item.java , itemLoader.java , GameScreen)

게임 종료시 타이머를 통한 본인의 생존 시간을 메인화면으로 돌아가서 표시, 2인 이상일 경우 높은 생존 시간을 가진 플레이어가 승리했다고 표시. ( GameLogic.java )
![StartMenu](https://github.com/Jominjun1/DodgeGame/assets/116476333/40a65667-a2cf-4708-a1bf-c91df9ac8d5c)


![GameInfo](https://github.com/Jominjun1/DodgeGame/assets/116476333/3d2db6d4-49d9-4caf-a7f0-d805b5f414e8)
