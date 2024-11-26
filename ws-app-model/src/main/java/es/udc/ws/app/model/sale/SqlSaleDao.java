package es.udc.ws.app.model.sale;

import es.udc.ws.app.model.matchservice.exceptions.MismatchedCardNumberException;
import es.udc.ws.app.model.matchservice.exceptions.TicketsAlreadyCollectedException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.Connection;
import java.util.List;

public interface SqlSaleDao {

    public Sale create(Connection connection, Sale sale);

    public Sale find(Connection connection, Long saleId)
            throws InstanceNotFoundException;

    public void update(Connection connection, Sale sale)
            throws InstanceNotFoundException;

    public void remove(Connection connection, Long saleId)
            throws InstanceNotFoundException;

    public List<Sale> findSalesByUser(Connection connection, String userId);
}