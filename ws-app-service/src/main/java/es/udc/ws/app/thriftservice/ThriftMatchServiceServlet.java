package es.udc.ws.app.thriftservice;

import es.udc.ws.app.thrift.ThriftMatchService;
import es.udc.ws.util.servlet.ThriftHttpServletTemplate;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;

public class ThriftMatchServiceServlet extends ThriftHttpServletTemplate {
    public ThriftMatchServiceServlet() {
        super(createProcessor(), createProtocolFactory());
    }

    private static TProcessor createProcessor() {

        return new ThriftMatchService.Processor<ThriftMatchService.Iface>(
                new ThriftMatchServiceImpl());

    }

    private static TProtocolFactory createProtocolFactory() {
        return new TBinaryProtocol.Factory();
    }
}
