package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import test.pageobjects.*;
import test.util.SeleniumUtils;


//Ordenamos las pruebas por el nombre del método
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MyWallapopTests {
	
	
	static String PathFirefox65 = "C:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe";
	static String Geckdriver022 = "C:\\Users\\User\\Google Drive\\universidad\\SDI\\PL-Material\\PL-SDI-Sesión5-material\\geckodriver024win64.exe";
	
	static WebDriver driver = getDriver(PathFirefox65, Geckdriver022);
	static String URLRemota = "http://ec2-3-83-65-177.compute-1.amazonaws.com:8080"; 
	static String URLLocal = "http://localhost:8080"; 
	static String URL = URLRemota;

	public static WebDriver getDriver(String PathFirefox, String Geckdriver) {
		System.setProperty("webdriver.firefox.bin", PathFirefox);
		System.setProperty("webdriver.gecko.driver", Geckdriver);
		WebDriver driver = new FirefoxDriver();
		return driver;
	}
	
	//Antes de cada prueba se navega al URL home de la aplicaciónn
		@Before
		public void setUp(){
			driver.navigate().to(URL);
		}

		//Después de cada prueba se borran las cookies del navegador
		@After
		public void tearDown(){
			driver.manage().deleteAllCookies();
		}

		//Antes de la primera prueba
		@BeforeClass
		static public void begin() throws ParseException {
			InsertDataMongo insertDataMongo = new InsertDataMongo();
			insertDataMongo.dataInsertion();
		}

		//Al finalizar la última prueba
		@AfterClass
		static public void end() {
			//Cerramos el navegador al finalizar las pruebas
			driver.quit();
		}
		
		/**
		 * Registro de Usuario con datos válidos
		 */
		@Test
		public void test01() {
			PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
			PO_RegisterView.fillForm(driver, "joseperez@email.com", "Josefo", "Perez", "77777",
					"77777");
			PO_RegisterView.checkKey(driver, "authenticated.message", PO_Properties.getSPANISH() );
		}
		
		/**
		 * Registro de Usuario con datos inválidos (email vacío, nombre vacío, apellidos vacíos).
		 */
		@Test
		public void test02() {
			PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
			PO_RegisterView.fillForm(driver, "", "", "", "77777",
					"77777");
			PO_RegisterView.checkKey(driver, "signup.message", PO_Properties.getSPANISH() );
		}
		
		/**
		 * Registro de Usuario con datos inválidos (repetición de contraseña inválida).
		 */
		@Test
		public void test03() {
			PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
			PO_RegisterView.fillForm(driver, "joseperez@email.com", "Josefo", "Perez", "77777",
					"77778");
			PO_RegisterView.checkKey(driver, "Error.signup.passwordConfirm.coincidence", PO_Properties.getSPANISH() );
		}
		
		/**
		 * Registro de Usuario con datos inválidos (email existente).
		 */
		@Test
		public void test04() {
			PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
			PO_RegisterView.fillForm(driver, "pedrod@email.com", "Pedro", "Díaz", "123456",
					"123456");
			PO_RegisterView.checkKey(driver, "Error.signup.mail.duplicate", PO_Properties.getSPANISH() );
		}
		
		/**
		 * Inicio de sesión con datos válidos (administrador).
		 */
		@Test
		public void test05() {
			PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "admin@email.com" , "admin" );
			PO_View.checkKey(driver, "users.administration", PO_Properties.getSPANISH() );
		}
		
		/**
		 * Inicio de sesión con datos válidos (usuario estándar).
		 */
		@Test
		public void test06() {
			PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "pedrod@email.com" , "123456" );
			PO_View.checkKey(driver, "offers.administration", PO_Properties.getSPANISH() );
		}
		
		/**
		 * Inicio de sesión con datos inválidos (usuario estándar, campo email y contraseña vacios).
		 */
		@Test
		public void test07() {
			PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "" , "" );
			PO_LoginView.checkKey(driver, "login.message", PO_Properties.getSPANISH() );
		}
		
		/**
		 * Inicio de sesión con datos válidos (usuario estándar, email existente, pero contraseña incorrecta).
		 */
		@Test
		public void test08() {
			PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "pedrod@email.com" , "1234567" );
			PO_LoginView.checkKey(driver, "login.message", PO_Properties.getSPANISH() );
		}
		
		/**
		 * Inicio de sesión con datos válidos (usuario estándar, email no existente en la aplicación).
		 */
		@Test
		public void test09() {
			PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "pedrod@email.com" , "1234567" );
			PO_LoginView.checkKey(driver, "login.message", PO_Properties.getSPANISH() );
		}
		
		/**
		 * Hacer click en la opción de salir de sesión y comprobar que se redirige a la página de login
		 */
		@Test
		public void test10() {
			PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "pedrod@email.com" , "123456" );
			PO_HomeView.clickOption(driver, "logout", "class", "btn btn-primary");
			PO_LoginView.checkKey(driver, "login.message", PO_Properties.getSPANISH() );
		}
		
		/**
		 * Comprobar que el botón cerrar sesión no está visible si el usuario no está autenticado
		 */
		@Test
		public void test11() {
			PO_LoginView.checkKey(driver, "login.message", PO_Properties.getSPANISH() );
			SeleniumUtils.EsperaCargaPaginaNoTexto(driver, "logout.message",PO_View.getTimeout() );
		}
		
		/**
		 * Mostrar el listado de usuarios y comprobar que se muestran todos los que existen en el sistema
		 */
		@Test
		public void test12() {
			PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "admin@email.com" , "admin" );
			PO_View.checkKey(driver, "users.administration", PO_Properties.getSPANISH() );
			List<WebElement> elementos = PO_View.checkElement(driver, "free",
				"//li[contains(@id, 'users-menu')]/a");
			elementos.get(0).click();
			elementos = PO_View.checkElement(driver, "free", "//a[contains(@href,'user/list')]");
			elementos.get(0).click();
			elementos = SeleniumUtils.EsperaCargaPagina(driver, "free",
				"//tbody/tr", PO_View.getTimeout());
			assertTrue(elementos.size() == 5);
		}
		
		/**
		 * Ir a la lista de usuarios, borrar el primer usuario de la lista, comprobar que la lista se actualiza y dicho usuario desaparece.
		 */
		@Test
		public void test13() {
			PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "admin@email.com" , "admin" );
			PO_View.checkKey(driver, "users.administration", PO_Properties.getSPANISH() );
			List<WebElement> elementos = PO_View.checkElement(driver, "free","//li[contains(@id, 'users-menu')]/a");
			elementos.get(0).click();
			elementos = PO_View.checkElement(driver, "free", "//a[contains(@href,'user/list')]");
			elementos.get(0).click();
			elementos = SeleniumUtils.EsperaCargaPagina(driver, "free",	"//tbody/tr", PO_View.getTimeout());
			elementos = SeleniumUtils.EsperaCargaPagina(driver, "class", "eliminar", PO_View.getTimeout());
			elementos.get(0).click();
			elementos = SeleniumUtils.EsperaCargaPagina(driver, "class", "eliminar", PO_View.getTimeout());

			assertTrue(elementos.size() == 4);
		}
		

		/**
		 * Ir a la lista de usuarios, borrar el ultimo usuario de la lista, comprobar que la lista se actualiza y dicho usuario desaparece.
		 */
		@Test
		public void test14() {
			PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "admin@email.com" , "admin" );
			PO_View.checkKey(driver, "users.administration", PO_Properties.getSPANISH() );
			List<WebElement> elementos = PO_View.checkElement(driver, "free","//li[contains(@id, 'users-menu')]/a");
			elementos.get(0).click();
			elementos = PO_View.checkElement(driver, "free", "//a[contains(@href,'user/list')]");
			elementos.get(0).click();
			elementos = SeleniumUtils.EsperaCargaPagina(driver, "free",	"//tbody/tr", PO_View.getTimeout());
			elementos = SeleniumUtils.EsperaCargaPagina(driver, "class", "eliminar", PO_View.getTimeout());
			elementos.get(elementos.size()-1).click();
			int tam = elementos.size();
			elementos = SeleniumUtils.EsperaCargaPagina(driver, "class", "eliminar", PO_View.getTimeout());

			assertTrue(elementos.size() == tam-1);
		}
		/**
		 * Ir a la lista de usuarios, borrar tres usuario de la lista, comprobar que la lista se actualiza y dicho usuario desaparece.
		 */
		@Test
		public void test15() {
			PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "admin@email.com" , "admin" );
			PO_View.checkKey(driver, "users.administration", PO_Properties.getSPANISH() );
			List<WebElement> elementos = PO_View.checkElement(driver, "free","//li[contains(@id, 'users-menu')]/a");
			elementos.get(0).click();
			elementos = PO_View.checkElement(driver, "free", "//a[contains(@href,'user/list')]");
			elementos.get(0).click();
			elementos = SeleniumUtils.EsperaCargaPagina(driver, "free",	"//tbody/tr", PO_View.getTimeout());
			elementos = SeleniumUtils.EsperaCargaPagina(driver, "class", "checkbox", PO_View.getTimeout());
			int tam = elementos.size();
			elementos.get(0).click();
			elementos.get(1).click();
			elementos.get(2).click();
			elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "eliminarVarios", PO_View.getTimeout());
			elementos.get(0).click();
			
			
			elementos = SeleniumUtils.EsperaCargaPagina(driver, "class", "checkbox", PO_View.getTimeout());

			assertEquals(elementos.size(),tam -3);
		}
		
		/**
		 * Ir al formulario de alta de oferta, rellenarla con datos válidos y pulsar el botón Submit.Comprobar que la oferta sale en el listado de ofertas de dicho usuario
		 */
		@Test
		public void test16() {
			PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "mariarguez@email.com" , "123456" );
			List<WebElement> elementos = PO_View.checkElement(driver, "id","offers-menu");
			elementos.get(0).click();
			elementos = PO_View.checkElement(driver, "free", "//a[contains(@href,'/offer/add')]");
			elementos.get(0).click();
			elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "description", PO_View.getTimeout());
			PO_AddOfferView.fillForm(driver, "Producto Test", "detalles del producto", "50.00");
			
			elementos = PO_View.checkElement(driver, "free","//li[contains(@id, 'offers-menu')]/a");
			elementos.get(0).click();
			elementos = PO_View.checkElement(driver, "free", "//a[contains(@href,'/offer/own')]");
			elementos.get(0).click();
			
			elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "description", PO_View.getTimeout());
			boolean existe = false;
			for(WebElement e: elementos)
			{
				if(e.getText().equals("Producto Test"))
				{
					existe = true;
				}
			}
			assertTrue(existe);
		}
		
		/**
		 * Ir al formulario de alta de oferta, rellenarla con datos inválidos (campo título vacío) y pulsar el botón Submit. Comprobar que se muestra el mensaje de campo obligatorio.
		 */
		@Test
		public void test17() {
			PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "mariarguez@email.com" , "123456" );
			List<WebElement> elementos = PO_View.checkElement(driver, "id","offers-menu");
			elementos.get(0).click();
			elementos = PO_View.checkElement(driver, "free", "//a[contains(@href,'/offer/add')]");
			elementos.get(0).click();
			PO_AddOfferView.fillForm(driver, "", "detalles del producto", "50.00");
			PO_AddOfferView.checkKey(driver, "offer.details", PO_Properties.getSPANISH());
		}
		
		/**
		 * Mostrar el listado de ofertas para dicho usuario y comprobar que se muestran todas los que existen para este usuario. 
		 */
		@Test
		public void test18() {
			PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "mariarguez@email.com" , "123456" );
			List<WebElement> elementos = PO_View.checkElement(driver, "id","offers-menu");
			elementos.get(0).click();
			elementos = PO_View.checkElement(driver, "free", "//a[contains(@href,'/offer/own')]");
			elementos.get(0).click();
			elementos = PO_View.checkElement(driver, "class", "eliminar");
			
			assertEquals(1,elementos.size());
			
		}
		
		
		
		
		
		/**
		 * Ir a la lista de ofertas, borrar la primera oferta de la lista, comprobar que la lista se actualiza y
		 * que la oferta desaparece.
		 */
		@Test
		public void test19() {
			PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "pedrod@email.com" , "123456" );
			PO_View.checkKey(driver, "offers.administration", PO_Properties.getSPANISH() );
			List<WebElement> elementos = PO_View.checkElement(driver, "free",
				"//li[contains(@id, 'offers-menu')]/a");
			elementos.get(0).click();
			elementos = PO_View.checkElement(driver, "free", "//a[contains(@href,'offer/own')]");
			elementos.get(0).click();
			elementos = PO_View.checkElement(driver, "free", 
					"//td[contains(text(), 'Mochila')]/following-sibling::*/a[contains(@href, 'offer/delete')]");
			elementos.get(0).click();
			SeleniumUtils.EsperaCargaPaginaNoTexto(driver, "Mochila",PO_View.getTimeout() );
		}
		
		/**
		 * Ir a la lista de ofertas, borrar la última oferta de la lista, comprobar que la lista se actualiza y
		 * que la oferta desaparece.
		 */
		@Test
		public void test20() {
			PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "pedrod@email.com" , "123456" );
			PO_View.checkKey(driver, "offers.administration", PO_Properties.getSPANISH() );
			List<WebElement> elementos = PO_View.checkElement(driver, "free",
				"//li[contains(@id, 'offers-menu')]/a");
			elementos.get(0).click();
			elementos = PO_View.checkElement(driver, "free", "//a[contains(@href,'offer/own')]");
			elementos.get(0).click();
			elementos = PO_View.checkElement(driver, "free", 
					"//td[contains(text(), 'Lápices')]/following-sibling::*/a[contains(@href, 'offer/delete')]");
			elementos.get(0).click();
			SeleniumUtils.EsperaCargaPaginaNoTexto(driver, "Lápices",PO_View.getTimeout() );
		}
		
		/**
		 * Hacer una búsqueda con el campo vacío y comprobar que se muestra la página que
		 * corresponde con el listado de las ofertas existentes en el sistema
		 */
		@Test
		public void test21() {
			PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "mariarguez@email.com" , "123456" );
			List<WebElement> elementos = PO_View.checkElement(driver, "id","offers-menu");
			elementos.get(0).click();
			elementos = PO_View.checkElement(driver, "free", "//a[contains(@href,'/offer/search')]");
			elementos.get(0).click();
			elementos = PO_View.checkElement(driver, "id", "id");
			String id = elementos.get(0).getText();			
			elementos = PO_View.checkElement(driver, "id", "buscar");
			elementos.get(0).click();
			elementos = PO_View.checkElement(driver, "id", "id");
			assertEquals(id,elementos.get(0).getText());				
		}
		
		/**
		 * Hacer una búsqueda escribiendo en el campo un texto que no exista y comprobar que se
		 * muestra la página que corresponde, con la lista de ofertas vacía.
		 */
		@Test
		public void test22() {
			PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "mariarguez@email.com" , "123456" );
			List<WebElement> elementos = PO_View.checkElement(driver, "id","offers-menu");
			elementos.get(0).click();
			elementos = PO_View.checkElement(driver, "free", "//a[contains(@href,'/offer/search')]");
			elementos.get(0).click();
			PO_SearchView.fillForm(driver, "qwertyui");
			PO_View.checknoElement(driver, "id", "id");	
		}
		
		/**
		 * Sobre una búsqueda determinada (a elección de desarrollador), comprar una oferta que deja
		 * un saldo positivo en el contador del comprobador. Y comprobar que el contador se actualiza
		 * correctamente en la vista del comprador.
		 */
		@Test
		public void test23() {
			PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "mariarguez@email.com" , "123456" );
			List<WebElement> elementos = PO_View.checkElement(driver, "id","offers-menu");
			elementos.get(0).click();
			elementos = PO_View.checkElement(driver, "free", "//a[contains(@href,'/offer/search')]");
			elementos.get(0).click();
			PO_SearchView.fillForm(driver, "Mochila");
			elementos = PO_View.checkElement(driver, "class", "btn");
			elementos.get(1).click();
			elementos = PO_View.checkElement(driver, "class", "balance");
			assertEquals("70.0",elementos.get(0).getText());		
		
		}
		
		/**
		 * Sobre una búsqueda determinada (a elección de desarrollador), comprar una oferta que deja
		 * un saldo 0 en el contador del comprobador. Y comprobar que el contador se actualiza correctamente en
	  	 * la vista del comprador.  
	  	 * */
		@Test
		public void test24() {
			PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "mariarguez@email.com" , "123456" );
			List<WebElement> elementos = PO_View.checkElement(driver, "id","offers-menu");
			elementos.get(0).click();
			elementos = PO_View.checkElement(driver, "free", "//a[contains(@href,'/offer/search')]");
			elementos.get(0).click();
			PO_SearchView.fillForm(driver, "Estuche");
			elementos = PO_View.checkElement(driver, "class", "btn");
			elementos.get(1).click();
			elementos = PO_View.checkElement(driver, "class", "balance");
			assertEquals("0.0",elementos.get(0).getText());
		
		}
		
		/**
		 * Sobre una búsqueda determinada (a elección de desarrollador), intentar comprar una oferta
		 * que esté por encima de saldo disponible del comprador. Y comprobar que se muestra el mensaje de
		 * saldo no suficiente. 
		 * */
		@Test
		public void test25() {
			PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "mariarguez@email.com" , "123456" );
			List<WebElement> elementos = PO_View.checkElement(driver, "id","offers-menu");
			elementos.get(0).click();
			elementos = PO_View.checkElement(driver, "free", "//a[contains(@href,'/offer/search')]");
			elementos.get(0).click();
			PO_SearchView.fillForm(driver, "Lápices");
			elementos = PO_View.checkElement(driver, "class", "btn");
			elementos.get(1).click();
			driver.switchTo().alert().accept();
			elementos = PO_View.checkElement(driver, "class", "balance");
			assertEquals("100.0",elementos.get(0).getText());
		}
		
		/**
		 * Ir a la opción de ofertas compradas del usuario y mostrar la lista. Comprobar que aparecen
		 * las ofertas que deben aparecer.
		 * */
		@Test
		public void test26() {
			PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "mariarguez@email.com" , "123456" );
			List<WebElement> elementos = PO_View.checkElement(driver, "id","offers-menu");
			elementos.get(0).click();
			elementos = PO_View.checkElement(driver, "free", "//a[contains(@href,'/offer/bought')]");
			elementos.get(0).click();
			elementos = PO_View.checkElement(driver, "class", "eliminar");
			assertEquals(2,elementos.size());
		}
		
		/**
		 * Visualizar al menos cuatro páginas en Español/Inglés/Español (comprobando que algunas
		 * de las etiquetas cambian al idioma correspondiente). Página principal/Opciones Principales de
		 * Usuario/Listado de Usuarios de Admin/Vista de alta de Oferta.
		 */
		@Test
		public void test27() {
			//Página principal
			PO_HomeView.checkChangeIdiom(driver, "btnSpanish", "btnEnglish",
					PO_Properties.getSPANISH(), PO_Properties.getENGLISH());
			//Opciones principales de usuario
			PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "pedrod@email.com" , "123456" );
			PO_View.checkKey(driver, "offers.administration", PO_Properties.getSPANISH() );
			PO_NavView.changeIdiom(driver, "btnEnglish");
			PO_View.checkKey(driver, "offers.administration", PO_Properties.getENGLISH() );
			PO_NavView.changeIdiom(driver, "btnSpanish");
			PO_View.checkKey(driver, "offers.administration", PO_Properties.getSPANISH() );
			//Vista de alta de oferta
			List <WebElement> elementos = PO_View.checkElement(driver, "free",
					"//li[contains(@id, 'offers-menu')]/a");
			elementos.get(0).click();
			elementos = PO_View.checkElement(driver, "free", "//a[contains(@href,'offer/add')]");
			elementos.get(0).click();
			PO_View.checkKey(driver, "user", PO_Properties.getSPANISH() );
			PO_NavView.changeIdiom(driver, "btnEnglish");
			PO_View.checkKey(driver, "user", PO_Properties.getENGLISH() );
			PO_NavView.changeIdiom(driver, "btnSpanish");
			PO_View.checkKey(driver, "user", PO_Properties.getSPANISH() );
			PO_HomeView.clickOption(driver, "logout", "class", "btn btn-primary");
			//Listado de usuarios de admin
			PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "admin@email.com" , "admin" );
			PO_View.checkKey(driver, "users.administration", PO_Properties.getSPANISH() );
			elementos = PO_View.checkElement(driver, "free",
					"//li[contains(@id, 'users-menu')]/a");
			elementos.get(0).click();
			elementos = PO_View.checkElement(driver, "free", "//a[contains(@href,'user/list')]");
			elementos.get(0).click();
			PO_View.checkKey(driver, "user.system", PO_Properties.getSPANISH() );
			PO_NavView.changeIdiom(driver, "btnEnglish");
			PO_View.checkKey(driver, "user.system", PO_Properties.getENGLISH() );
			PO_NavView.changeIdiom(driver, "btnSpanish");
			PO_View.checkKey(driver, "user.system", PO_Properties.getSPANISH() );
		}
		
		/**
		 * Intentar acceder sin estar autenticado a la opción de listado de usuarios del administrador.
		 */
		@Test
		public void test28() {
			PO_View.checkKey(driver, "login.message", PO_Properties.getSPANISH() );
			SeleniumUtils.EsperaCargaPaginaNoTexto(driver, "Gestión de usuarios",PO_View.getTimeout() );
		}
		
		/**
		 * Intentar acceder sin estar autenticado a la opción de listado de ofertas propias de
		 * un usuario estándar
		 */
		@Test
		public void test29() {
			PO_View.checkKey(driver, "login.message", PO_Properties.getSPANISH() );
			SeleniumUtils.EsperaCargaPaginaNoTexto(driver, "Gestión de ofertas",PO_View.getTimeout() );
		}
		
		/**
		 * Estando autenticado como usuario estándar intentar acceder a la opción de listado de
		 * usuarios del administrador.
		 */
		@Test
		public void test30() {
			PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "pedrod@email.com" , "123456" );
			SeleniumUtils.EsperaCargaPaginaNoTexto(driver, "Gestión de usuarios",PO_View.getTimeout() );
		}
		
		
		
		

}
