package com.netty.third;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author zhangmeng
 * @date 2019/9/1
 * @function
 * 1.当 有其他 客户端和服务端建立或者 断开连接 则 发送消息给 已经建立连接的可以端 告知新客户端建立连接或者离开连接
 * 2.当 客户端发送消息给服务端，则服务端将消息广播给所有客户端，如果是自己则特殊标记
 */
public class MyChatServerHandler extends SimpleChannelInboundHandler<String> {

    //定义当前遍历用户存放所有建立连接的客户端
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {

        Channel channel = ctx.channel();

        channelGroup.forEach(ch->{
            if(channel != ch){
                ch.writeAndFlush(channel.remoteAddress() +"发送的消息" + msg +"" + "\n");
            }else {
                ch.writeAndFlush("[自己]"+ msg + "\n");
            }
        });
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();

        channelGroup.writeAndFlush("[服务端]" +channel.remoteAddress() + "加入\n");

        channelGroup.add(channel);

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();

        channelGroup.writeAndFlush("[服务端]" +channel.remoteAddress() + "离开\n");

        System.out.println(channelGroup.size());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();

        System.out.println(channel.remoteAddress() + "上线");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();

        System.out.println(channel.remoteAddress() + "下线");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.close();
    }
}
