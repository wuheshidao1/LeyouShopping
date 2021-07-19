package com.leyou.mail.listener;

import com.leyou.mail.utils.MailUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;

@Component
public class MailListener {

    @Autowired
    private MailUtils mailUtils;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "leyou.mail.queue",durable = "true"),
            exchange = @Exchange(value = "leyou.mail.exchange",ignoreDeclarationExceptions = "true",type = ExchangeTypes.TOPIC),
            key = {"verifycode.mail"}
    ))
    public void sendMail(Map<String,String> msg) throws Exception {
        if (CollectionUtils.isEmpty(msg)){
            return;
        }
        String mail = msg.get("mail");
        String code = msg.get("code");
        if (StringUtils.isNoneBlank(mail)&&StringUtils.isNoneBlank(code)){
            mailUtils.sendMail(mail,code);
        }
    }
}
