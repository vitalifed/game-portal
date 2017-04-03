$(function() {

	$("#room-submit").click(function(e) {
		e.preventDefault();

		Room.name = $("#roomname").val()
		Room.user.name = $("#username").val()

		var res = Room.connect()
		console.log(res)

		if (res != null)
			$('#panel-room-id').append("<div class='notice notice-warning'><strong>Warning</strong> " + res + "</div>")
	});

	$('#btn-leave-id').click(function(e) {
		e.preventDefault();
		Room.disconnect(null);
	});

	$(".opponent-turn-msg").hide();
	$(".player-turn-msg").hide();

	$("[id^=me-cell]").hover(function(e) {
		if ($(this).html() != '0' && Game.locked == 0)
			$(this).addClass('player-point')
	}, function(e) {
		$(this).removeClass('player-point')
	})

});

var Page = {

	message : function(cmd) {
		var msg = cmd.payload;

		var id = '#panel-messages-id'
		if ($('#landing-page').is(":visible")) {
			id = '#panel-room-id'
		}

		if (msg && msg.message) {
			if (msg.type == 'INFO') {
				console.log(id)
				$("<div class='notice notice-info'><strong>Info</strong> " + msg.message + "</div>").appendTo($(id)).hide().show('slow')
			} else if (msg.type == 'WARNING') {
				$("<div class='notice notice-warning'><strong>Warning</strong> " + msg.message + "</div>").appendTo($(id)).hide().show('slow')
			}
			if ($(id).children().length > 5) {
				$(id).find('div:first').remove()
			}
		}
	},

	connected : function(success) {
		if (success) {
			$('#landing-page').hide()
			$('#playground-page').show()
			$('#panel-messages-id').children(".notice").remove();
		} else {
			$('#landing-page').show()
			$('#playground-page').hide()
			$('#panel-room-id').children(".notice").remove();
		}
	},

	leave : function() {
		Page.connected(false)
	}
}

var Game = {

	locked : 1,

	init : function(cmd) {
		$("[id^=opp-cell]").html('6')
		$("[id^=me-cell]").html('6')

		$("#me-score").html('0')
		$("#opp-score").html('0')

		$("[id^=me-cell]").unbind("click")
	},

	start : function(cmd) {

		$("[id^=me-cell]").click(function(e) {
			if (Game.locked == 1 || $(this).html() == '0') {
				e.preventDefault();
				return;
			}
			Game.locked = 1
			id = $(this).attr("id");

			Game.step(id.substring(id.length - 1))
		})
	},

	move : function(cmd) {
		// Enable UI
		Game.locked = 0;

		$("td.player-turn").addClass("highlight");
		$("tr.player-side").removeClass("disabled");
		$(".player-turn-msg").show();

		console.log('My move')
	},

	step : function(idx) {
		Room.step(idx)

		$("td.player-turn").removeClass("highlight");
		$("td.opponent-turn").removeClass("highlight");
		$("tr.opponent-side").addClass("disabled");
		$("tr.player-side").addClass("disabled");
		$(".opponent-turn-msg").hide();
		$(".player-turn-msg").hide();

	},

	state : function(cmd) {
		console.log('Update state')
		if (cmd.payload != null) {
			obj = cmd.payload[0];

			$("#me-score").html(obj.score)
			for (idx in obj.state) {
				$("#me-cell" + idx).html(obj.state[idx])
			}

			obj = cmd.payload[1];

			$("#opp-score").html(obj.score)
			for (idx in obj.state) {
				$("#opp-cell" + idx).html(obj.state[idx])
			}
		}

	},

	stop : function(cmd) {
		// At the moment there is nothing to do
		Page.message({
			payload : {
				message : "Game is over, please leave the room",
				type : "WARNING"
			}
		});
	}

}
