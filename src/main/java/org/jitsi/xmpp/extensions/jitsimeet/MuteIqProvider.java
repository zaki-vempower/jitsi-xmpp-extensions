/*
 * Copyright @ 2018 - present 8x8, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jitsi.xmpp.extensions.jitsimeet;

import org.jivesoftware.smack.provider.*;
import org.jitsi.utils.logging.*;
import org.jxmpp.jid.*;
import org.jxmpp.jid.impl.*;
import org.xmlpull.v1.*;

/**
 * The parser of {@link MuteIq}.
 *
 * @author Pawel Domas
 */
public class MuteIqProvider
    extends IQProvider<MuteIq>
{
        /**
     * The logger instance used by this class.
     */
    private final static Logger logger
        = Logger.getLogger(MuteIqProvider.class);

    /**
     * Registers this IQ provider into given <tt>ProviderManager</tt>.
     */
    public static void registerMuteIqProvider()
    {
        ProviderManager.addIQProvider(
            MuteIq.ELEMENT_NAME,
            MuteIq.NAMESPACE,
            new MuteIqProvider());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MuteIq parse(XmlPullParser parser, int initialDepth)
        throws Exception
    {
        String namespace = parser.getNamespace();

        // Check the namespace
        if (!MuteIq.NAMESPACE.equals(namespace))
        {
            return null;
        }

        String rootElement = parser.getName();

        MuteIq iq;

        if (MuteIq.ELEMENT_NAME.equals(rootElement))
        {
            iq = new MuteIq();
            String jidStr = parser.getAttributeValue("", MuteIq.JID_ATTR_NAME);
            if (jidStr != null)
            {
                Jid jid = JidCreate.from(jidStr);
                iq.setJid(jid);
            }

            String lockStr = parser.getAttributeValue("", MuteIq.LOCK_AUDIO_DISABLE);
            if (lockStr != null)
            {
                Boolean lockMute = Boolean.parseBoolean(lockStr);
                logger.info("lockstr" + lockStr + "lockA" + lockMute);
                iq.setLock(lockMute);
            }

            String actorStr
                = parser.getAttributeValue("", MuteIq.ACTOR_ATTR_NAME);
            if (actorStr != null)
            {
                Jid actor = JidCreate.from(actorStr);
                iq.setActor(actor);
            }
        }
        else
        {
            return null;
        }

        boolean done = false;

        while (!done)
        {
            switch (parser.next())
            {
                case XmlPullParser.END_TAG:
                {
                    String name = parser.getName();

                    if (rootElement.equals(name))
                    {
                        done = true;
                    }
                    break;
                }

                case XmlPullParser.TEXT:
                {
                    Boolean mute = Boolean.parseBoolean(parser.getText());
                    iq.setMute(mute);
                    break;
                }
            }
        }

        return iq;
    }
}
