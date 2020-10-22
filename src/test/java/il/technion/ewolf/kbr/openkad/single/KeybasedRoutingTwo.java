package il.technion.ewolf.kbr.openkad.single;

import com.google.inject.Guice;
import com.google.inject.Injector;
import il.technion.ewolf.kbr.KeybasedRouting;
import il.technion.ewolf.kbr.Node;
import il.technion.ewolf.kbr.openkad.KadNetModule;
import junit.framework.Assert;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName: KeybasedRoutingTwo
 * @Description:
 * @Author: VT
 * @Date: 2020-10-22 17:28
 */
@Slf4j
public class KeybasedRoutingTwo {
    public static void main(String[] args) throws IOException, URISyntaxException {
        int basePort = 10000;
            Injector injector = Guice.createInjector(new KadNetModule()
                    .setProperty("openkad.keyfactory.keysize", "1")
                    .setProperty("openkad.bucket.kbuckets.maxsize", "3")
                    .setProperty("openkad.seed", ""+(basePort+1))
                    .setProperty("openkad.net.udp.host", "127.0.0.1")
                    .setProperty("openkad.net.udp.port", ""+(basePort+1)));


            KeybasedRouting kbr = injector.getInstance(KeybasedRouting.class);
            kbr.create();

        kbr.join(Arrays.asList(new URI("openkad.udp://127.0.0.1:"+basePort+"/")));
        log.info("finished joining");
        List<Node> neighbours = kbr.getNeighbours();
        log.info("neighbours:{}",neighbours);
    }
}
