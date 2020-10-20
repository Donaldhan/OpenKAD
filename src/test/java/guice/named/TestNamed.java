package guice.named;

import com.google.inject.*;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

/**
 * @ClassName: TestNamed
 * @Description:
 * @Author: VT
 * @Date: 2020-10-20 20:13
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
        bind(IHelloPrinter.class).annotatedWith(Names.named("simple")).to(SimpleHelloPrinter.class);
        bind(IHelloPrinter.class).annotatedWith(Names.named("complex")).to(ComplexHelloPrinter.class);
    }

}

@Singleton
public class TestNamed {

    @Inject
    @Named("simple")
    private IHelloPrinter simplePrinter;

    @Inject
    @Named("complex")
    private IHelloPrinter complexPrinter;

    public void hello() {
        simplePrinter.print();
        complexPrinter.print();
    }

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new SampleModule());
        TestNamed sample = injector.getInstance(TestNamed.class);
        sample.hello();
    }

}