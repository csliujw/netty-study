package cn.enhance.chart.server.handler;

import cn.enhance.chart.message.LoginRequestMessage;
import cn.enhance.chart.message.LoginResponseMessage;
import cn.enhance.chart.server.ChatServer;
import cn.enhance.chart.server.service.UserServiceFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class LoginRequestMessageHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
        System.out.println("read?!!");
        String username = msg.getUsername();
        String password = msg.getPassword();
        boolean login = UserServiceFactory.getUserService().login(username, password);
        LoginResponseMessage message;
        if (login) {
            System.out.format("%s", login + "");
            message = new LoginResponseMessage(true, "登录成功");
        } else {
            System.out.format("%s", login + "");
            message = new LoginResponseMessage(false, "用户名或密码错误");
        }
        log.debug("发送数据");
        ctx.writeAndFlush(message);
    }
}

