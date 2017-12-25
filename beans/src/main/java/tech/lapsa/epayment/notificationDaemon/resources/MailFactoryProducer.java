package tech.lapsa.epayment.notificationDaemon.resources;

import static tech.lapsa.epayment.notificationDaemon.drivenBeans.Constants.*;

import javax.annotation.Resource;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import javax.mail.Session;

import tech.lapsa.lapsa.mail.MailBuilderException;
import tech.lapsa.lapsa.mail.MailFactory;
import tech.lapsa.lapsa.mail.impl.SessionMailFactory;

@Singleton
public class MailFactoryProducer {

    @Resource(mappedName = JNDI_MAIL_USER)
    private Session userMailSession;

    @Produces
    @QRecipientUser
    public MailFactory userMailFactory() throws MailBuilderException {
	return new SessionMailFactory(userMailSession);
    }

}
