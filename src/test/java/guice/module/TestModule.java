package guice.module;

import com.google.inject.*;

/**
 * @ClassName: TestModule
 * @Description:
 * @Author: VT
 * @Date: 2020-10-20 20:10
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
        bind(IHelloPrinter.class).to(SimpleHelloPrinter.class);
    }

}

@Singleton
public class TestModule {

    @Inject
    private IHelloPrinter printer;

    public void hello() {
        printer.print();
    }

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new SampleModule());
        TestModule sample = injector.getInstance(TestModule.class);
        sample.hello();
    }

}