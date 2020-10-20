package guice.collection.map;

import com.google.inject.*;
import com.google.inject.multibindings.MapBinder;

import java.util.Map;

/**
 * @ClassName: TestMap
 * @Description:
 * @Author: VT
 * @Date: 2020-10-20 20:18
 */
interface IHelloPrinter {
    void print();
}

@Singleton
class SimpleHelloPrinter implements IHelloPrinter {

    public void print() {
        System.out.println("Hello, Simple World");
    }

}

@Singleton
class ComplexHelloPrinter implements IHelloPrinter {

    public void print() {
        System.out.println("Hello, Complex World");
    }

}

class SampleModule extends AbstractModule {

    @Override
    protected void configure() {
        MapBinder<String, IHelloPrinter> printers = MapBinder.newMapBinder(binder(), String.class, IHelloPrinter.class);
        printers.addBinding("simple").to(SimpleHelloPrinter.class);
        printers.addBinding("complex").to(ComplexHelloPrinter.class);
    }

}

@Singleton
public class TestMap {

    @Inject
    private Map<String, IHelloPrinter> printers;

    public void hello() {
        for (String name : printers.keySet()) {
            printers.get(name).print();
        }
    }

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new SampleModule());
        TestMap sample = injector.getInstance(TestMap.class);
        sample.hello();
    }

}