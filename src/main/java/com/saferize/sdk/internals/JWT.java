package com.saferize.sdk.internals;

import java.io.IOException;
import java.io.StringReader;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import com.saferize.sdk.Configuration;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

class JWT {

	private Configuration config;
	private RSAPrivateKey privateKey;
	
	public JWT(Configuration config) throws JWTException {
		this.config = config;
		try {
			privateKey = readKey(config.getPrivateKey());
		} catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException e) {
			throw new JWTException(e);
		}
	}
	
	public String generateJWT() {		
		String jwt = Jwts.builder()
			.setSubject(config.getAccessKey())		
			.setHeaderParam("typ", "JWT")
			.setAudience("https://saferize.com/principal")
			.setExpiration(new Date(new Date().getTime() + 30000))
			.signWith(privateKey, SignatureAlgorithm.RS256)
			.compact();
        return jwt;
	}
	
    public RSAPrivateKey readKey(String privateKey) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
    	Security.addProvider(new BouncyCastleProvider());   	
    	try (PEMParser parser = new PEMParser(new StringReader(privateKey))) {
        	Object object = parser.readObject();
        	JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
        	PEMKeyPair ukp = (PEMKeyPair) object;
        	return (RSAPrivateKey) converter.getKeyPair(ukp).getPrivate();    		
    	}
    }
	
}
