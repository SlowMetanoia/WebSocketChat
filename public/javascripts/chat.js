/**
 * Кодина для веб-сокета
 */
//поля
const inputField = document.getElementById("chatInput")
const outputArea     = document.getElementById("chat-area")

//сокет
const socketRoute = document.getElementById("ws-route").value
const socket = new WebSocket(socketRoute.replace("http","ws"))


//спам
const spamButton = document.getElementById("spam-button")
const delay = async (ms) => await new Promise(resolve => setTimeout(resolve, ms))
const spamSize = 1000

//украшательства
const sendButton = document.getElementById("send-button")

sendButton.onclick = () => {
    socket.send(inputField.value)
    inputField.value = ''
}

inputField.onkeydown = (event) =>{
    if(event.key === 'Enter'){
        socket.send(inputField.value)
        inputField.value = ''
    }
}
socket.onopen = ()=>socket.send("New user connected.")
socket.onmessage = (event) => {
    outputArea.value += event.data + '\n'
    outputArea.scrollTop = outputArea.scrollHeight
}
socket.onclose = ()=>{
     alert("Oops, u have been disconnected, reload this page")
}

spamButton.onclick = ()=>{
    socket.send("spamStart")
    console.log("spamStart")
    for (let i = 0; i<spamSize;i++) {
        //delay(10).then(socket.send("spamString" + i))
        console.log("spamString" + i)
        socket.send("spamString" + i)
    }
    console.log("spamEnd")
    socket.send("spamEnd")
}