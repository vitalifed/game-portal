Mancala - Multiplayer game based on websocket communication protocol 
====================================================================

Mancala is one form the families of board games and this is a version of the basic game.
To get familiar with rules of game, take a look into [Mancala::Wiki](https://en.wikipedia.org/wiki/Mancala)

# Composition 

The project is developed based upon open architecture and microservices concepts, where every new module can be built as independent deployable package. 
The architecture of the project does not focus on solving only a specific task, but rather open to extending, and creating any multiplayer games.

## How does it work in particular ?

The client establishes a connection with an endpoint and sends a request to a controller:

**See mancala.js**

```javascript

		Room.socket = new SockJS(Room.websocket);
		Room.stompClient = Stomp.over(Room.socket);

		Room.stompClient.connect({}, function(frame) {
			console.log('Connected: ' + frame);

			Room.stompClient.subscribe('/topic/mancala/' + Room.uniqueId + '/' + Room.name + '/cmd',
					function(command) {
						console.log('/topic/mancala/' + Room.uniqueId + '/' + Room.name + '/cmd, Command: ' + command)
						var cmd = JSON.parse(command.body);

						Room.message(cmd);
						
						var commandToRun = eval('Room.' + cmd.command);
						console.log(commandToRun)
						
						if (commandToRun)
						    commandToRun(cmd)
					});
			Room.create(null);
		});


		Room.stompClient.send("/mancala/room/" + Room.name + '/create', {}, JSON.stringify({
			'name' : Room.user.name,
			'token' : Room.uniqueId
		}));


```

**Common module AbstractPortalController.java**
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

**mancala-game module MancalaPortalController.java**
```java

	@MessageMapping("/room/{room}/step/{idx}")
	public void stepGame(@Payload MancalaUser user, @DestinationVariable("room") String room,
			@DestinationVariable("idx") String idx) {
		if (logger.isDebugEnabled())
			logger.debug("Room=" + room + ", User=" + user + ". idx=" + idx);
		workflow.state(room, user, Integer.valueOf(idx)).operation(getSimpMessagingTemplate()).build().launch();
	}

```

The portal's controller works by processing incoming requests into a POJO, allowing further a workflow to makes a decision about required steps.
Just before sending a **Command.java** to listeners, **Workflow.java** applies specific parameters to these commands in a straightforward fashion. 

While this limits a abstract controller to only supporting POJO **User** and text-based parameter, it is dramatically simplified system aspects such as replaying requests.  

At the same time, a client subscribes as a listener on a single topic, where the payload of a response is a JSON transformed from POJO **Command.java** 
The client evaluates a command and executes it straightforward as a function.
 
**How to subscribe on topic**
```javascript

			Room.stompClient.subscribe('/topic/mancala/' + Room.uniqueId + '/' + Room.name + '/cmd',
					function(command) {
						console.log('/topic/mancala/' + Room.uniqueId + '/' + Room.name + '/cmd, Command: ' + command)
						var cmd = JSON.parse(command.body);

						Room.message(cmd);
						
						var commandToRun = eval('Room.' + cmd.command);
						console.log(commandToRun)
						
						if (commandToRun)
						    commandToRun(cmd)
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



