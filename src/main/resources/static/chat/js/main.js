'use strict';

// DOM Elements
const chatPage = document.querySelector('#chat-page');
const messageForm = document.querySelector('#messageForm');
const messageInput = document.querySelector('#message');
const messageArea = document.querySelector('#messageArea');
const connectingElement = document.querySelector('.connecting');

// WebSocket Client
let stompClient = null;

// Lấy tên người dùng từ URL path (phần cuối cùng của đường dẫn)
const pathParts = window.location.pathname.split('/');
let username = pathParts[pathParts.length - 2] || "Guest";
const isVendor = window.location.pathname.includes('/Vendor');
if (isVendor && username !== "Guest"){
 username = "Staff" // Nếu không tìm thấy, mặc định là "Guest"
}

// Kiểm tra nếu URL chứa "/Vendor", thay đổi tên người dùng để thêm "Nhân viên"
const isUser = window.location.pathname.includes('/User');
if (isUser && username !== "Guest") {
    username = generateRandomUsername(); // Tạo username ngẫu nhiên
}

function generateRandomUsername() {
    const prefix = "User_"; // Tiền tố username
    const randomId = Math.floor(Math.random() * 10000); // Số ngẫu nhiên từ 0 đến 9999
    return `${prefix}${randomId}`;
}
// Mảng màu cho avatar
const colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

// Kết nối WebSocket
function connect() {
    console.log("Connecting as " + username);

    const socket = new SockJS('/ws'); // Tạo kết nối SockJS
    stompClient = Stomp.over(socket);

    stompClient.connect({}, onConnected, onError);
}

// Khi kết nối thành công
function onConnected() {
    // Subscribe to the Public Topic
    stompClient.subscribe('/topic/public', onMessageReceived);

    // Gửi thông báo tham gia phòng chat
    stompClient.send("/app/chat.addUser", {}, JSON.stringify({ sender: username, type: 'JOIN' }));

    connectingElement.classList.add('hidden');
}

// Xử lý khi có lỗi kết nối
function onError(error) {
    connectingElement.textContent = 'Không thể kết nối đến máy chủ WebSocket. Vui lòng làm mới trang để thử lại!';
    connectingElement.style.color = 'red';

    // Tự động kết nối lại sau 5 giây
    setTimeout(() => {
        connect();
    }, 5000);
}

// Gửi tin nhắn
function sendMessage(event) {
    event.preventDefault();

    const messageContent = messageInput.value.trim();

    if (messageContent && stompClient) {
        const chatMessage = {
            sender: username,
            content: messageContent,
            type: 'CHAT'
        };

        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        messageInput.value = ''; // Xóa nội dung ô nhập sau khi gửi
    }
}

// Xử lý tin nhắn nhận được
function onMessageReceived(payload) {
    const message = JSON.parse(payload.body);

    const messageElement = document.createElement('li');
// Kiểm tra và hiển thị tin nhắn tham gia phòng chat
    if (message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = `${message.sender} đã tham gia phòng chat!`;
        messageElement.style.backgroundColor = '#DFF2BF';
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = `${message.sender} đã rời khỏi phòng chat!`;
        messageElement.style.backgroundColor = '#FFB6B9';
    } else {
        messageElement.classList.add('chat-message');
        messageElement.style.backgroundColor = '#F1F1F1';

        // Tạo avatar cho người gửi
        const avatarElement = document.createElement('i');
        const avatarText = document.createTextNode(message.sender[0].toUpperCase());
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);
        avatarElement.classList.add('avatar');
        messageElement.appendChild(avatarElement);

        // Hiển thị tên người gửi
        const usernameElement = document.createElement('span');
        usernameElement.textContent = message.sender;
        messageElement.appendChild(usernameElement);
    }

    // Hiển thị nội dung tin nhắn
    const textElement = document.createElement('p');
    textElement.textContent = message.content;
    messageElement.appendChild(textElement);

    // Thêm tin nhắn vào vùng hiển thị
    messageArea.appendChild(messageElement);

    // Cuộn xuống dưới cùng
    messageArea.scrollTop = messageArea.scrollHeight;
}

// Lấy màu sắc cho avatar
function getAvatarColor(messageSender) {
    let hash = 0;
    for (let i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }

    const index = Math.abs(hash % colors.length);
    return colors[index];
}

// Sự kiện gửi tin nhắn khi nhấn nút gửi
messageForm.addEventListener('submit', sendMessage, true);

// Kết nối khi tải trang xong
window.onload = connect;

// Gửi thông báo rời phòng khi đóng trang
window.onbeforeunload = function () {
    if (stompClient) {
        stompClient.send("/app/chat.removeUser", {}, JSON.stringify({ sender: username, type: 'LEAVE' }));
    }
};

// Gửi tin nhắn khi nhấn Enter
messageInput.addEventListener('keydown', function(event) {
    if (event.key === "Enter" && !event.shiftKey) {
        sendMessage(event);
    }
});