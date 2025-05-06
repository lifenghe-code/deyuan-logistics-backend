package com.monitor.sms.utils;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class SmsUtil {
    @Resource
    Client smsClient;

    public SendSmsResponse sendSms(String phone) throws Exception {
        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setPhoneNumbers(phone)
                .setSignName("阿里云")
                .setTemplateCode("SMS_15305****")
                .setTemplateParam("{\"name\":\"张三\",\"number\":\"1390000****\"}");
        SendSmsResponse sendSmsResponse = smsClient.sendSms(sendSmsRequest);
        return sendSmsResponse;
    }


}
