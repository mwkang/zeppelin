package org.apache.zeppelin.oauth;

import static org.apache.zeppelin.conf.ZeppelinConfiguration.ConfVars.ZEPPELIN_OAUTH20_ACCESS_TOKEN_EXTRACTOR_CLASSNAME;
import static org.apache.zeppelin.conf.ZeppelinConfiguration.ConfVars.ZEPPELIN_OAUTH20_ACCESS_TOKEN_HTTP_METHOD;
import static org.apache.zeppelin.conf.ZeppelinConfiguration.ConfVars.ZEPPELIN_OAUTH20_ACCESS_TOKEN_URL;
import static org.apache.zeppelin.conf.ZeppelinConfiguration.ConfVars.ZEPPELIN_OAUTH20_AUTHORIZATION_BASE_URL;
import static org.apache.zeppelin.conf.ZeppelinConfiguration.ConfVars.ZEPPELIN_OAUTH20_REFRESH_TOKEN_URL;
import static org.apache.zeppelin.conf.ZeppelinConfiguration.ConfVars.ZEPPELIN_OAUTH20_SIGNATURE_TYPE;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.Verb;
import org.apache.commons.lang3.StringUtils;
import org.apache.zeppelin.conf.ZeppelinConfiguration;
import org.apache.zeppelin.conf.ZeppelinConfiguration.ConfVars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicApi20 extends DefaultApi20 {

  private static final Logger LOG = LoggerFactory.getLogger(BasicApi20.class);

  protected BasicApi20() {
    ZeppelinConfiguration conf = ZeppelinConfiguration.create();
    boolean enable = conf.getBoolean(ConfVars.ZEPPELIN_OAUTH20_ENABLE);
    if (!enable) {
      LOG.info("Do not use oauth2.0");
      return;
    }
    LOG.info("use oauth2.0");

    //TODO-minwoo if required not exist, log error
    Verb verb = Verb.valueOf(conf.getString(ZEPPELIN_OAUTH20_ACCESS_TOKEN_HTTP_METHOD));
    String accessTokenUrl = conf.getString(ZEPPELIN_OAUTH20_ACCESS_TOKEN_URL);
    if (StringUtils.isEmpty(accessTokenUrl)) {
      throw new IllegalArgumentException(
          ZEPPELIN_OAUTH20_ACCESS_TOKEN_URL.getVarName() + " must set");
    }

    String refreshTokenUrl = conf.getString(ZEPPELIN_OAUTH20_REFRESH_TOKEN_URL);
    if (StringUtils.isEmpty(refreshTokenUrl)) {
      LOG.info("Using '{}' in refreshTokenUrl. Because refreshTokenUrl is empty.",
          accessTokenUrl); //TODO-minwoo 로그 영작 하기...
      refreshTokenUrl = accessTokenUrl;
    }

    String authorizationBaseUrl = conf.getString(ZEPPELIN_OAUTH20_AUTHORIZATION_BASE_URL);
    if (StringUtils.isEmpty(authorizationBaseUrl)) {
      throw new IllegalArgumentException(
          ZEPPELIN_OAUTH20_AUTHORIZATION_BASE_URL.getVarName() + " must set");
    }

    String signatureType = conf.getString(ZEPPELIN_OAUTH20_SIGNATURE_TYPE);
    String accessTokenExtractorClassName = conf
        .getString(ZEPPELIN_OAUTH20_ACCESS_TOKEN_EXTRACTOR_CLASSNAME);
    if (StringUtils.isEmpty(accessTokenExtractorClassName)) {
      LOG.info("Using '{}' in accessTokenExtractorClassName");
    }

    //TODO-minwoo make oauth20
  }

  private static class InstanceHolder {

    private static final BasicApi20 INSTANCE = new BasicApi20();
  }

  public static BasicApi20 instance() {
    return InstanceHolder.INSTANCE;
  }

  @Override
  public String getAccessTokenEndpoint() {
    return null;
  }

  @Override
  protected String getAuthorizationBaseUrl() {
    return null;
  }
}
