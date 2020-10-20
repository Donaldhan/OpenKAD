package guice.collection.set;

import com.google.inject.*;
import com.google.inject.multibindings.Multibinder;

import java.util.Set;

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
        Multibinder<IHelloPrinter> printers = Multibinder.newSetBinder(binder(), IHelloPrinter.class);
        printers.addBinding().to(SimpleHelloPrinter.class);
        printers.addBinding().to(ComplexHelloPrinter.class);
    }

}

@Singleton
public class TestSet {

    @Inject
    private Set<IHelloPrinter> printers;

    public void hello() {
        for (IHelloPrinter printer : printers) {
            printer.print();
        }
    }

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new SampleModule());
        TestSet sample = injector.getInstance(TestSet.class);
        sample.hello();
    }

}