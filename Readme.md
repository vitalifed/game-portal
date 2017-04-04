Mancala - Multiplayer game based on websocket communication protocol
====================================================================

[TOC]

Mancala is one form the families of board games and this is a version of the basic game.
To get familiar with rules of game, take a look into [Mancala::Wiki](https://en.wikipedia.org/wiki/Mancala)

# Composition

The project is developed based upon open architecture and microservices concepts, where every new module can be built as independent deployable package.
The architecture of the project does not focus on solving only a specific task, but rather open to extending, and creating any multiplayer games.

## How does it work in particular ?

The client establishes a connection with an endpoint and sends a request to a controller:

**See room.full.js**

```javascript

		Room.socket = new SockJS(Room.websocket);
		Room.stompClient = Stomp.over(Room.socket);

		Room.stompClient.connect({}, function(frame) {
			console.log('Connected: ' + frame);

			Room.stompClient.subscribe('/topic'+Room.contextRoot+'/' + Room.uniqueId + '/' + Room.name + '/cmd',
					function(command) {
						console.log('/topic'+Room.contextRoot+'/' + Room.uniqueId + '/' + Room.name + '/cmd, Command: ' + command)
						var cmd = JSON.parse(command.body);

						var commandToRun = eval('Room.' + cmd.command);
						console.log(commandToRun)

						if (commandToRun)
						    commandToRun(cmd)

						Room.message(cmd);

					});
			Room.create(null);
		});


		Room.stompClient.send(Room.contextRoot + "/room/" + Room.name + '/create', {}, JSON.stringify({
			'name' : Room.user.name,
			'token' : Room.uniqueId
		}));


```

The portal's controller works by processing incoming requests into a POJO, allowing further a workflow to make a decision about required steps.
Just before sending a **Command.java** to listeners, **Workflow.java** applies specific parameters to these commands in a straightforward fashion.

While this limits a abstract controller to only supporting POJO **User.java** and text-based parameter, it is dramatically simplified system aspects such as replaying requests.  

At the same time, a client subscribes as a listener on a single topic, where the payload of a response is a JSON which is obtained from POJO **Command.java**
The client evaluates a command and executes it straightforward as a function.


**See in common module AbstractPortalController.java**
```java

	@MessageMapping("/room/{room}/create")
	public void create(@Payload ActualUser user, @DestinationVariable("room") String room) {
		getWorkflow().createRoom(room, user).operation(getSimpMessagingTemplate()).build().launch();
	}

	@MessageMapping("/room/{room}/leave")
	public void leave(@Payload ActualUser user, @DestinationVariable("room") String room) {
		getWorkflow().leaveRoom(room, user)
				.operation(simpMessagingTemplate).build().launch();
	}

```

**See in mancala-game module MancalaPortalController.java**
```java

	@MessageMapping("/room/{room}/step/{idx}")
	public void stepGame(@Payload MancalaUser user, @DestinationVariable("room") String room,
			@DestinationVariable("idx") String idx) {
		if (logger.isDebugEnabled())
			logger.debug("Room=" + room + ", User=" + user + ". idx=" + idx);
		workflow.state(room, user, Integer.valueOf(idx)).operation(getSimpMessagingTemplate()).build().launch();
	}

```


**How to subscribe on topic**
```javascript

			Room.stompClient.subscribe('/topic'+Room.contextRoot+'/' + Room.uniqueId + '/' + Room.name + '/cmd',
					function(command) {
						console.log('/topic'+Room.contextRoot+'/' + Room.uniqueId + '/' + Room.name + '/cmd, Command: ' + command)
						var cmd = JSON.parse(command.body);

						var commandToRun = eval('Room.' + cmd.command);
						console.log(commandToRun)

						if (commandToRun)
						    commandToRun(cmd)

						Room.message(cmd);

					});

```

**Example of Command**
```JSON

	{
		"command": "subscribe",
		"payload": {
			"message": "Welcome to Mancala: Vitali",
			"type": "INFO"
		}
	}

```
```JSON

	{
		"command": "state",
		"payload": {
			"score": 0,
			"state": [6,6,6,6,6,6]
		}
	}

```

The **Workflow.java** is a central implementation of the area of expertise that straightforward manages the logic and data of application.
**Workflow** operates in the terms of application logic and provides a response via builder abstraction,
where the resulting controller is communicated to the client via **Launcher.java** and underlying **Command.java**

**See Workflow.java**
```java
	public interface Workflow<ActualUser extends User, ActualGame extends Game<ActualUser>> {
	
		CommandBuilder<?> createRoom(String room, ActualUser user);
		
		CommandBuilder<?> leaveRoom(String room, ActualUser user);
		
		CommandBuilder<?> startGame(String room);
		
		CommandBuilder<?> stopGame(String room, ActualGame game);
			
	}
```

**See CommandBuilder.java**
```java
	public interface CommandBuilder<T extends Launcher<?>> {

		CommandBuilder<T> operation(SimpMessageSendingOperations operation);

		CommandBuilder<T> topic(String topic);

		CommandBuilder<T> command(String command);

		T build();
	}
```

**See Launcher.java**
```java
public interface Launcher<T> {

	void launch();

	Command<T> getCommand();
}
```

### Repository

The data manipulation is implemented through the single responsibility interface **RoomRepository.java**

```java
	public interface RoomRepository<ActualUser extends User, ActualGame extends Game<ActualUser>> {

		public ActualGame get(Room room);

		public int addUser(Room room, ActualUser user) throws GamePortalException;

		public ActualUser[] removeUser(Room room, ActualUser user);

		void removeRoom(Room room);

		void init(Room room);
	}
```


## Configuration

**See application.properties**
```properties
	#Set up the topic that broker should listen
	broker.topic=/topic
	
	#Context root for both: controllers and client, make sure it is the same here and in room.full.js, Room.contextRoot 
	context.root=/mancala
	
	#End-point for both: controllers and client, make sure it is the same here and in room.full.js, Room.websocket 
	end.point=/game-mancala-websocket
	
	#The application is communicated to the client by means of producer-consumer technic, the topic will be defined 
	#during first communication with a client 
	topic.path.cmd=/topic${context.root}/#{'$'}{token}/#{'$'}{room}/cmd

```

## Security

Essentially, the client generates a securely delegated token (aka Room.uniqueId, see room.full.js), the client then uses the token to 
perform mutual communication with server. The http conversation can be secured by means of TLS out of the box.

## Performance management

In the nutshell, **RoomRepository** declines a creation of a room if the total amount exceeds a threshold, default value equals 100. 
Moreover, the implementation of repository introduces a cache with eviction policy: 
* each entry should be automatically removed from the cache once a fixed duration has elapsed after 
** the entry's creation
** the most recent replacement of its value, 
** or its last access

The default elapsed time is 10 minutes.

# How to build and run application

Run in **game-portal** 
$ mvn install

Run in **game-portal/mancala-game**

$ mvn spring-boot:run

Open a link in a browser http://localhost:8080/

## How to run tests

By default junit and integration tests are disabled, in order to build a projects and run test use the following:

$ mvn install -Dmaven.test.skip=false