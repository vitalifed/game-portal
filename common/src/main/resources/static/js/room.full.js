var Room = {

	uniqueId : guid(),
	//Next two fields should be initialised
	contextRoot : '',
	websocket : '',

	name : null,
	user : {
		name : null
	},

	socket : null,
	stompClient : null,

	connect : function() {

		console.log(this)

		var message = Room.validateBeforeConnect()
		if (message != null) {
			console.log(message)
			return message;
		}
		console.log('Opening socket...')
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

		return null;
	},

	validateBeforeConnect : function() {
		if (Room.name == null || Room.name == '')
			return 'Please specify room\'s name'
		if (Room.user == null || Room.user.name == null || Room.user.name == '')
			return 'Please specify user name'

		return null
	},

	// Commands
	disconnect : function(cmd) {
		Room.leave(cmd);
		Room.refuse(cmd);
	},

	subscribe : function(cmd) {
		Page.connected(true);
	},

	refuse : function(cmd) {
		if (Room.stompClient != null) {
			Room.stompClient.disconnect();
		}
		Room.socket = null;
		Room.stompClient = null;
		Page.connected(false);
		
		console.log("Disconnected");
	},

	// Channels
	create : function(cmd) {
		Room.stompClient.send(Room.contextRoot + "/room/" + Room.name + '/create', {}, JSON.stringify({
			'name' : Room.user.name,
			'token' : Room.uniqueId
		}));
		console.log('Sent ' + Room.user)
	},
	
	message : function(cmd) {
		if (cmd && cmd.payload && cmd.payload.message) {
			console.log('Room.message ' + cmd.payload.message)
			Page.message(cmd)
		}
	},	

	leave : function(cmd) {
		Page.leave(cmd)
		Room.stompClient.send(Room.contextRoot + "/room/" + Room.name + '/leave', {}, JSON.stringify({
			'name' : Room.user.name,
			'token' : Room.uniqueId
		}));
		
		Game.init(cmd)
	},

	step : function(idx) {
		Room.stompClient.send(Room.contextRoot + "/room/" + Room.name + '/step/'+idx, {}, JSON.stringify({
			'name' : Room.user.name,
			'token' : Room.uniqueId
		}));
		console.log('User ' + Room.user+" made a step: "+idx)
	},
	
	start : function(cmd){
		Game.init(cmd)
		Game.start(cmd)
	},
	
	move : function(cmd){
		Game.move(cmd)
	},
	
	state : function(cmd){
		console.log(cmd)
		Game.state(cmd)
	},
	
	stop : function(cmd){
		Game.stop(cmd)
	}

}

function guid() {
	function s4() {
		return Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1);
	}
	return s4() + s4() + '-' + s4() + '-' + s4() + '-' + s4() + '-' + s4() + s4() + s4();
}