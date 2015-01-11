var Cylon = require('cylon');
var bodyParser = require('body-parser')
var express = require('express')

var mMy;
var currColor = [255,255,255];

function setCurrColor(r,g,b) {
    currColor = [r,g,b];
}

//function flashColor() {
//    var factor = 500;
//    for(var i=1; i < 7 ; i+2) {
//        setTimeout(function() {
//            changeColor(0,0,0);
//        }, i * factor);
//        setTimeout(function() {
//            changeColor(currColor[0],currColor[1],currColor[2]);
//        }, (i + 1) * factor);
//    }
//}

var flashInterval;
var flashColor = function(factor){
    flashInterval = setInterval(function() {
        changeColor(0,0,0);
        setTimeout(function(){
            changeColor(currColor[0],currColor[1],currColor[2]);
        }, factor / 2)
    }, factor);
    setTimeout(function() {
        clearInterval(flashInterval)
    }, factor * 7)
}

function changeColor(r,g,b) {
    mMy.lcd.setColor(r,g,b)
}

var raveInterval;
function rave() {
    var r = Math.random() * 255;
    var g = Math.random() * 255;
    var b = Math.random() * 255;
    
    changeColor(r,g,b);
    
    raveInterval = setInterval(rave, 300);
}

//function backlight(on) {
//    //on ? mMy.lcd. : mMy.lcd.;
//}



function changeText(text) {
    
    
    switch(text) {
        case 'red': changeColor(255,0,0);setCurrColor(255,0,0); break;
        case 'orange': changeColor(255,165,0);setCurrColor(255,165,0); break;
        case 'yellow': changeColor(255,255,0);setCurrColor(255,255,0); break;
        case 'green': changeColor(0,255,0);setCurrColor(0,255,0); break;
        case 'blue': changeColor(0,0,255); setCurrColor(0,0,255);break;
        case 'purple': changeColor(128,0,128); setCurrColor(128,0,128);break;
        case 'pink': changeColor(255,192,203);setCurrColor(255,142,153); break;
        case 'white': changeColor(255,255,255);setCurrColor(255,255,255); break;
            
        //case 'on' : backlight(true); break;
        //case 'off': backlight(false); break;
        
        case 'sit':   flashColor(100); break;
        case 'rollover':flashColor(150); break;
        case 'stay':  flashColor(200); break;
        case 'down':  flashColor(250); break;
        case 'come':  flashColor(300); break;
            
        case 'rave': rave(); break;
        case 'stop': clearInterval(raveInterval); break;
        
    }
    
    mMy.lcd.setCursor(0,0)
    mMy.lcd.write(text + "                       ")
    
}

Cylon
    .robot()
    .connection('edison', { adaptor: 'intel-iot' })
    .device('lcd', { driver: 'upm-jhd1313m1', connection: 'edison' })
    .on('ready', function(my) {
        mMy = my
//        mMy.lcd.setCursor(5,0);
        changeText("Welcome!")
        
    });

Cylon.start();

var app = express()

app.use(bodyParser.urlencoded({
    extended: true
}));

app.get('/', function (req, res) {
    console.log("got the get")
    res.send("got it")
})

app.post('/', function (req, res) {
    console.log(req.body.shout)
    changeText(req.body.shout)
    res.send("got it")
})

var server = app.listen(3000, function () {
    
    var host = server.address().address
    var port = server.address().port
    
    console.log("Listening at http://%s:%s", host, port)
    
})