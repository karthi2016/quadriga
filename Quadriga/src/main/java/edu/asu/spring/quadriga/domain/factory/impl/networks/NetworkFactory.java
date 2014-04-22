package edu.asu.spring.quadriga.domain.factory.impl.networks;

import org.springframework.stereotype.Service;

import edu.asu.spring.quadriga.domain.factory.networks.INetworkFactory;
import edu.asu.spring.quadriga.domain.impl.dictionary.Dictionary;
import edu.asu.spring.quadriga.domain.impl.networks.Network;
import edu.asu.spring.quadriga.domain.network.INetwork;

/**
 * Factory class for creating {@link Dictionary}.
 * 
 * @author Lohith Dwaraka
 *
 */
@Service
public class NetworkFactory implements INetworkFactory {
	public INetwork createNetworkObject() {
		
		return new Network();
	}

}