package vn.iotstar.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import vn.iotstar.dto.ChatMessageDTO;
import vn.iotstar.dto.SessionInfoDTO;

@Controller
public class ChatController {

	// Phương thức hiển thị trang chat cho người dùng thông thường
	@GetMapping("/User/chat")
	public String showChatPage(Model model)
	{
		try {
			System.out.println("USER VENDOR");
			return "chat/index"; // Trả về trang chat chính
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/login"; // Chuyển hướng đến trang đăng nhập nếu có lỗi
		}
	}

	// Phương thức hiển thị trang chat cho người dùng từ /employee
	@GetMapping("/Vendor/chat")
	public String showEmployeeChatPage(Model model) {
		try {
			System.out.println("CHAT VENDOR");
			return "chat/index"; // Trả về trang chat chính
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/login"; // Chuyển hướng đến trang đăng nhập nếu có lỗi
		}
	}

	// Phương thức gửi SessionInfoname vào WebSocket session
	@MessageMapping("/chat.addSessionInfo")
	@SendTo("/topic/public")
	public ChatMessageDTO addSessionInfo(@Payload ChatMessageDTO chatMessage, SimpMessageHeaderAccessor headerAccessor) {
		SessionInfoDTO SessionInfoDTO = (SessionInfoDTO) headerAccessor.getSessionAttributes().get("SessionInfoDTO");

		if (SessionInfoDTO == null) {
			SessionInfoDTO = new SessionInfoDTO("anonymousSessionInfo"); // Nếu không có SessionInfoDTO, tạo mới
		}

		headerAccessor.getSessionAttributes().put("SessionInfoDTO", SessionInfoDTO);
		chatMessage.setSender(SessionInfoDTO.getFullname()); // Gửi tên người dùng vào tin nhắn

		return chatMessage;
	}

	// Phương thức gửi tin nhắn
	@MessageMapping("/chat.sendMessage")
	@SendTo("/topic/public")
	public ChatMessageDTO sendMessage(@Payload ChatMessageDTO chatMessage) {
		return chatMessage;
	}

	@MessageMapping("/chat.addUser")
	@SendTo("/topic/public")
	public ChatMessageDTO addUser(@Payload ChatMessageDTO chatMessage) {
		chatMessage.setType(ChatMessageDTO.MessageType.JOIN);
		return chatMessage;
	}

	@MessageMapping("/chat.removeUser")
	@SendTo("/topic/public")
	public ChatMessageDTO removeUser(@Payload ChatMessageDTO chatMessage) {
		chatMessage.setType(ChatMessageDTO.MessageType.LEAVE); // Đảm bảo set type là LEAVE
		return chatMessage;
	}
}
