package guice.implemented;

import com.google.inject.*;

/**
 * @ClassName: TestSampleImplementedBy
 * @Description:
 * @Author: VT
 * @Date: 2020-10-20 19:45
 */
@ImplementedBy(SimpleHelloPrinter.class)
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

@Singleton
public class TestSampleImplementedBy {

    @Inject
    private IHelloPrinter printer;

    public void hello() {
        printer.print();
    }

    public static void main(String[] args) {
        Injector injector = Guice.createInjector();
        TestSampleImplementedBy sample = injector.getInstance(TestSampleImplementedBy.class);
        sample.hello();
    }

}