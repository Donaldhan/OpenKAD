package guice.constructor;

import com.google.inject.*;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

/**
 * @ClassName: TestConstructor
 * @Description:
 * @Author: VT
 * @Date: 2020-10-20 20:16
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
public class TestConstructor {

    @Named("simple")
    private IHelloPrinter simplePrinter;

    @Named("complex")
    private IHelloPrinter complexPrinter;

    @Inject
    public TestConstructor(@Named("simple") IHelloPrinter simplePrinter, @Named("complex") IHelloPrinter complexPrinter) {
        this.simplePrinter = simplePrinter;
        this.complexPrinter = complexPrinter;
    }

    public void hello() {
        simplePrinter.print();
        complexPrinter.print();
    }

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new SampleModule());
        TestConstructor sample = injector.getInstance(TestConstructor.class);
        sample.hello();
    }

}