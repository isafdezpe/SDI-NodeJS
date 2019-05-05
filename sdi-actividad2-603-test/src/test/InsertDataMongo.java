package test;

import java.text.ParseException;
import java.util.Date;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class InsertDataMongo {
	
	private MongoClient mongoClient;
    private MongoDatabase mongodb;
    
    public void connectDatabase() {
		try {
			setMongoClient(new MongoClient(new MongoClientURI(
					"mongodb://admin:sdi@mywallapop-shard-00-00-dprkm.mongodb.net:27017,mywallapop-shard-00-01-dprkm.mongodb.net:27017,mywallapop-shard-00-02-dprkm.mongodb.net:27017/test?ssl=true&replicaSet=mywallapop-shard-0&authSource=admin&retryWrites=true")));
			setMongodb(getMongoClient().getDatabase("test"));
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}
    
    public void insertOneDataTest() throws ParseException {
		try {
			//Insertar usuarios
			MongoCollection<Document> usuarios = getMongodb().getCollection("usuarios");
			Document usuario1 = new Document().append("nombre", "Administrador").append("apellidos", "admin")
					.append("email", "admin@email.com")
					.append("password", "ebd5359e500475700c6cc3dd4af89cfd0569aa31724a1bf10ed1e3019dcfdb11")
					.append("saldo", 100).append("rol", "admin");
			usuarios.insertOne(usuario1);
			Document usuario2 = new Document().append("nombre", "Isabel").append("apellidos", "Fernandez")
					.append("email", "isabelf@email.com")
					.append("password", "6fabd6ea6f1518592b7348d84a51ce97b87e67902aa5a9f86beea34cd39a6b4a")
					.append("saldo", 100).append("rol", "standard");
			usuarios.insertOne(usuario2);
			Document usuario3 = new Document().append("nombre", "Luis").append("apellidos", "Garcia")
					.append("email", "luisg@email.com")
					.append("password", "6fabd6ea6f1518592b7348d84a51ce97b87e67902aa5a9f86beea34cd39a6b4a")
					.append("saldo", 100).append("rol", "standard");
			usuarios.insertOne(usuario3);
			Document usuario4 = new Document().append("nombre", "Laura").append("apellidos", "Burguillo")
					.append("email", "laurab@email.com")
					.append("password", "6fabd6ea6f1518592b7348d84a51ce97b87e67902aa5a9f86beea34cd39a6b4a")
					.append("saldo", 100).append("rol", "standard");
			usuarios.insertOne(usuario4);
			Document usuario5 = new Document().append("nombre", "Gabriel").append("apellidos", "Naya")
					.append("email", "gabrieln@email.com")
					.append("password", "6fabd6ea6f1518592b7348d84a51ce97b87e67902aa5a9f86beea34cd39a6b4a")
					.append("saldo", 66).append("rol", "standard");
			usuarios.insertOne(usuario5);
			Document usuario6 = new Document().append("nombre", "Maria").append("apellidos", "Gonzalez")
					.append("email", "mariag@email.com")
					.append("password", "6fabd6ea6f1518592b7348d84a51ce97b87e67902aa5a9f86beea34cd39a6b4a")
					.append("saldo", 60).append("rol", "standard");
			usuarios.insertOne(usuario6);
			Document usuario7 = new Document().append("nombre", "Lucas").append("apellidos", "Rodriguez")
					.append("email", "lucasr@email.com")
					.append("password", "6fabd6ea6f1518592b7348d84a51ce97b87e67902aa5a9f86beea34cd39a6b4a")
					.append("saldo", 70).append("rol", "standard");
			usuarios.insertOne(usuario7);
			
			// Insertar ofertas
			MongoCollection<Document> ofertas = getMongodb().getCollection("ofertas");
			// Ofertas de isabelf
			Document ofertaCarpeta = new Document().append("nombre", "Carpeta").append("descripcion", "Carpeta de acordeón")
					.append("precio", 5).append("autor", usuario2.get("_id"))
					.append("fecha", new Date()).append("vendida", false)
					.append("autorEmail", "isabelf@email.com");
			ofertas.insertOne(ofertaCarpeta);
			ofertas.insertOne(new Document().append("nombre", "Altavoz").append("descripcion", "Altavoz portátil")
					.append("precio", 15).append("autor", usuario2.get("_id"))
					.append("fecha", new Date()).append("vendida", false)
					.append("autorEmail", "isabelf@email.com"));
			ofertas.insertOne(new Document().append("nombre", "Lápices").append("descripcion", "Lápices de colores")
					.append("precio", 4).append("autor", usuario2.get("_id"))
					.append("fecha", new Date()).append("vendida", true)
					.append("comprador", usuario5.get("_id"))
					.append("autorEmail", "isabelf@email.com"));
			ofertas.insertOne(new Document().append("nombre", "CD").append("descripcion", "CD de Operación Triunfo")
					.append("precio", 10).append("autor", usuario2.get("_id"))
					.append("fecha", new Date()).append("vendida", true)
					.append("comprador", usuario6.get("_id"))
					.append("autorEmail", "isabelf@email.com"));
			// Ofertas de luisg
			ofertas.insertOne(new Document().append("nombre", "Agenda").append("descripcion", "Agenda escolar 2019-2020")
					.append("precio", 10).append("autor", usuario3.get("_id"))
					.append("fecha", new Date()).append("vendida", false)
					.append("autorEmail", "luisg@email.com"));
			Document ofertaMonitor = new Document().append("nombre", "Monitor").append("descripcion", "Monitor de 20 pulgadas")
					.append("precio", 70).append("autor", usuario3.get("_id"))
					.append("fecha", new Date()).append("vendida", false)
					.append("autorEmail", "luisg@email.com");
			ofertas.insertOne(ofertaMonitor);
			ofertas.insertOne(new Document().append("nombre", "Libro").append("descripcion", "El código da Vinci")
					.append("precio", 10).append("autor", usuario3.get("_id"))
					.append("fecha", new Date()).append("vendida", true)
					.append("comprador", usuario7.get("_id"))
					.append("autorEmail", "luisg@email.com"));
			ofertas.insertOne(new Document().append("nombre", "Cajonera").append("descripcion", "Cajonera con 3 cajones")
					.append("precio", 40).append("autor", usuario3.get("_id"))
					.append("fecha", new Date()).append("vendida", true)
					.append("comprador", usuario5.get("_id"))
					.append("autorEmail", "luisg@email.com"));
			// Ofertas de laurab
			ofertas.insertOne(new Document().append("nombre", "Espejo").append("descripcion", "Espejo de tocador")
					.append("precio", 50).append("autor", usuario4.get("_id"))
					.append("fecha", new Date()).append("vendida", false)
					.append("autorEmail", "laurab@email.com"));
			ofertas.insertOne(new Document().append("nombre", "Consola").append("descripcion", "Nintendo 2DS")
					.append("precio", 48).append("autor", usuario4.get("_id"))
					.append("fecha", new Date()).append("vendida", false)
					.append("autorEmail", "laurab@email.com"));
			ofertas.insertOne(new Document().append("nombre", "Botas").append("descripcion", "Botas militares")
					.append("precio", 30).append("autor", usuario4.get("_id"))
					.append("fecha", new Date()).append("vendida", true)
					.append("comprador", usuario6.get("_id"))
					.append("autorEmail", "laurab@email.com"));
			ofertas.insertOne(new Document().append("nombre", "Libros de ESDLA").append("descripcion", "El Hobbit y La comunidad del anillo")
					.append("precio", 20).append("autor", usuario4.get("_id"))
					.append("fecha", new Date()).append("vendida", true)
					.append("comprador", usuario7.get("_id"))
					.append("autorEmail", "laurab@email.com"));
			
			// Insertar mensajes
			MongoCollection<Document> mensajes = getMongodb().getCollection("mensajes");
			mensajes.insertOne(new Document().append("emisor", "isabelf@email.com")
					.append("receptor", "luisg@email.com").append("idOferta", ofertaMonitor.get("_id"))
					.append("mensaje", "Hola, me interesa el monitor").append("fecha", new Date()));
			mensajes.insertOne(new Document().append("emisor", "luisg@email.com")
					.append("receptor", "isabelf@email.com").append("idOferta", ofertaCarpeta.get("_id"))
					.append("mensaje", "hola").append("fecha", new Date()));
			
		} catch (Exception ex) {
			System.out.print(ex.toString());
		}

	}

    public void removeDataTest() {
		getMongodb().getCollection("ofertas").drop();
		getMongodb().getCollection("usuarios").drop();
		getMongodb().getCollection("mensajes").drop();
	}
    
    public void dataInsertion() throws ParseException {
		InsertDataMongo javaMongodbInsertData = new InsertDataMongo();
		System.out.println("Conectando a Mongo");
		javaMongodbInsertData.connectDatabase();
		System.out.println("Eliminando datos");
		javaMongodbInsertData.removeDataTest();
		System.out.println("Insertando nuevos datos");
		javaMongodbInsertData.insertOneDataTest();
	}
    
    public MongoClient getMongoClient() {
		return mongoClient;
	}

	public void setMongoClient(MongoClient mongoClient) {
		this.mongoClient = mongoClient;
	}

	public MongoDatabase getMongodb() {
		return mongodb;
	}

	public void setMongodb(MongoDatabase mongodb) {
		this.mongodb = mongodb;
	}
}
