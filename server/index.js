var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);
var players = [];

server.listen(8080, function(){
	console.log("Server is now running");
});

io.on('connection', function(socket){
	console.log("Player connected");
	socket.emit('socketID', { id: socket.id });
	socket.emit('getPlayers', players)
	socket.broadcast.emit('newPlayer', { id: socket.id });
	socket.on('playerMoved', function(data){
	    data.id = socket.id;
	    socket.broadcast.emit('playerMoved', data);
	    for(var a = 0; a<players.length; a++){
	        if(players[a].id == data.id){
                players[a].x = data.x;
                players[a].y = data.y;
            }
	    }
	});

	socket.on('disconnect', function(){
		console.log("Player disconnected");
		socket.broadcast.emit('playerDisconnected', { id: socket.id });
		for(var i=0; i<players.length; i++){
		    if(players[i].id == socket.id){
		        players.splice(i, 1);
		    }
		}
	});
	players.push(new player(socket.id, 800, 50))
});

function player(id, x, y){
    this.id=id;
    this.x=x;
    this.y=y;
}