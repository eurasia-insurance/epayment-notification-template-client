package tech.lapsa.epayment.notifier.beans.producers;

import static tech.lapsa.epayment.notifier.beans.Constants.*;

import javax.annotation.Resource;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import javax.mail.Session;

import tech.lapsa.epayment.notifier.beans.qualifiers.QRecipientUser;
import tech.lapsa.javax.mail.MailBuilderException;
import tech.lapsa.javax.mail.MailFactory;
import tech.lapsa.javax.mail.impl.SessionMailFactory;

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
