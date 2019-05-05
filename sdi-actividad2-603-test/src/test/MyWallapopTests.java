package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.util.List;

import org.junit.*;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
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
	static String URL = "http://localhost:8081";

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
			PO_NavView.clickOption(driver, "registrarse", "class", "btn btn-primary");
			PO_RegisterView.fillForm(driver, "joseperez@email.com", "Josefo", "Perez", "77777",
					"77777");
			SeleniumUtils.textoPresentePagina(driver, "joseperez@email.com");
		}
		
		/**
		 * Registro de Usuario con datos inválidos (email vacío).
		 */
		@Test
		public void test02_1() {
			PO_NavView.clickOption(driver, "registrarse", "class", "btn btn-primary");
			PO_RegisterView.fillForm(driver, "", "Josefo", "Perez", "77777",
					"77777");
			SeleniumUtils.textoPresentePagina(driver, "Registrar usuario");
		}
		
		/**
		 * Registro de Usuario con datos inválidos (repetición de contraseña inválida).
		 */
		@Test
		public void test02_2() {
			PO_NavView.clickOption(driver, "registrarse", "class", "btn btn-primary");
			PO_RegisterView.fillForm(driver, "joseperez@email.com", "Josefo", "Perez", "77777",
					"77778");
			SeleniumUtils.textoPresentePagina(driver, "Registrar usuario");
			SeleniumUtils.textoPresentePagina(driver, "Las contraseñas no coinciden");
		}
		
		/**
		 * Registro de Usuario con datos inválidos (email existente).
		 */
		@Test
		public void test03() {
			PO_NavView.clickOption(driver, "registrarse", "class", "btn btn-primary");
			PO_RegisterView.fillForm(driver, "isabelf@email.com", "Isabel", "Fernandez", "123456",
					"123456");
			SeleniumUtils.textoPresentePagina(driver, "Registrar usuario");
			SeleniumUtils.textoPresentePagina(driver, "El email ya existe");
		}
		
		/**
		 * Inicio de sesión con datos válidos (administrador).
		 */
		@Test
		public void test04_1() {
			PO_NavView.clickOption(driver, "identificarse", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "admin@email.com" , "admin" );
			SeleniumUtils.textoPresentePagina(driver, "Listado de usuarios");
			SeleniumUtils.textoPresentePagina(driver, "admin@email.com");
			SeleniumUtils.textoPresentePagina(driver, "Cerrar sesión");
		}
		
		/**
		 * Inicio de sesión con datos válidos (usuario estándar).
		 */
		@Test
		public void test04_2() {
			PO_NavView.clickOption(driver, "identificarse", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "isabelf@email.com" , "123456" );
			SeleniumUtils.textoPresentePagina(driver, "Cerrar sesión");
			SeleniumUtils.textoPresentePagina(driver, "Ofertas propias");
			SeleniumUtils.textoPresentePagina(driver, "Compras");
			SeleniumUtils.textoPresentePagina(driver, "isabelf@email.com");
		}
		
		/**
		 * Inicio de sesión con datos inválidos (email existente, contraseña incorrecta).
		 */
		@Test
		public void test05() {
			PO_NavView.clickOption(driver, "identificarse", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "isabelf@email.com" , "12" );
			SeleniumUtils.textoPresentePagina(driver, "Identificación de usuario");
			SeleniumUtils.textoPresentePagina(driver, "Email o password incorrecto");
		}
		
		/**
		 * Inicio de sesión con datos válidos (email vacio).
		 */
		@Test
		public void test06_01() {
			PO_NavView.clickOption(driver, "identificarse", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "" , "123456" );
			SeleniumUtils.textoPresentePagina(driver, "Identificación de usuario");
		}
		
		/**
		 * Inicio de sesión con datos válidos (contraseña vacia).
		 */
		@Test
		public void test06_02() {
			PO_NavView.clickOption(driver, "identificarse", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "isabelf@email.com" , "" );
			SeleniumUtils.textoPresentePagina(driver, "Identificación de usuario");
		}
		
		/**
		 * Inicio de sesión con datos inválidos (email no existente en la aplicación).
		 */
		@Test
		public void test07() {
			PO_NavView.clickOption(driver, "identificarse", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "pedrod@email.com" , "123456" );
			SeleniumUtils.textoPresentePagina(driver, "Identificación de usuario");
			SeleniumUtils.textoPresentePagina(driver, "Email o password incorrecto");
		}
		
		/**
		 * Hacer click en la opción de salir de sesión y comprobar que se redirige a la página de login
		 */
		@Test
		public void test08() {
			PO_NavView.clickOption(driver, "identificarse", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "isabelf@email.com" , "123456" );
			PO_NavView.clickOption(driver, "desconectarse", "class", "btn btn-primary");
			SeleniumUtils.textoPresentePagina(driver, "Identificación de usuario");
		}
		
		/**
		 * Comprobar que el botón cerrar sesión no está visible si el usuario no está autenticado
		 */
		@Test
		public void test09() {
			SeleniumUtils.textoPresentePagina(driver, "Identifícate");
			SeleniumUtils.textoPresentePagina(driver, "Regístrate");
			SeleniumUtils.textoNoPresentePagina(driver, "Cerrar sesión");
		}
		
		/**
		 * Mostrar el listado de usuarios y comprobar que se muestran todos los que existen en el sistema
		 */
		@Test
		public void test10() {
			PO_NavView.clickOption(driver, "identificarse", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "admin@email.com" , "admin" );
			SeleniumUtils.textoPresentePagina(driver, "Gestión de usuarios");
			PO_NavView.clickOption(driver, "usuarios", "class", "btn btn-primary");
			List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, "free",
				"//tbody/tr", PO_View.getTimeout());
			assertTrue(elementos.size() == 6);
		}
		
		/**
		 * Ir a la lista de usuarios, borrar el primer usuario de la lista, comprobar que la lista se actualiza y dicho usuario desaparece.
		 */
		@Test
		public void test11() {
			PO_NavView.clickOption(driver, "identificarse", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "admin@email.com" , "admin" );
			SeleniumUtils.textoPresentePagina(driver, "Gestión de usuarios");
			PO_NavView.clickOption(driver, "usuarios", "class", "btn btn-primary");
			List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, "free",
				"//tbody/tr", PO_View.getTimeout());
			elementos = SeleniumUtils.EsperaCargaPagina(driver, "class", "checkbox", PO_View.getTimeout());
			elementos.get(0).click();
			elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "eliminarUsuarios", PO_View.getTimeout());
			elementos.get(0).click();

			elementos = SeleniumUtils.EsperaCargaPagina(driver, "free",
					"//tbody/tr", PO_View.getTimeout());
			assertTrue(elementos.size() == 5);
		}
		

		/**
		 * Ir a la lista de usuarios, borrar el ultimo usuario de la lista, comprobar que la lista se actualiza y dicho usuario desaparece.
		 */
		@Test
		public void test12() {
			PO_NavView.clickOption(driver, "identificarse", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "admin@email.com" , "admin" );
			SeleniumUtils.textoPresentePagina(driver, "Gestión de usuarios");
			PO_NavView.clickOption(driver, "usuarios", "class", "btn btn-primary");
			List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, "free",
				"//tbody/tr", PO_View.getTimeout());
			elementos = SeleniumUtils.EsperaCargaPagina(driver, "class", "checkbox", PO_View.getTimeout());
			elementos.get(elementos.size()-1).click();
			elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "eliminarUsuarios", PO_View.getTimeout());
			elementos.get(0).click();

			elementos = SeleniumUtils.EsperaCargaPagina(driver, "free",
					"//tbody/tr", PO_View.getTimeout());
			assertTrue(elementos.size() == 5);
		}
		/**
		 * Ir a la lista de usuarios, borrar tres usuario, comprobar que la lista se actualiza y dichos
		 *  usuarios desaparecen.
		 */
		@Test
		public void test13() {
			PO_NavView.clickOption(driver, "identificarse", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "admin@email.com" , "admin" );
			SeleniumUtils.textoPresentePagina(driver, "Gestión de usuarios");
			PO_NavView.clickOption(driver, "usuarios", "class", "btn btn-primary");
			List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, "free",
				"//tbody/tr", PO_View.getTimeout());
			elementos = SeleniumUtils.EsperaCargaPagina(driver, "class", "checkbox", PO_View.getTimeout());
			elementos.get(0).click();
			elementos.get(3).click();
			elementos.get(5).click();
			elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "eliminarUsuarios", PO_View.getTimeout());
			elementos.get(0).click();
			
			
			elementos = SeleniumUtils.EsperaCargaPagina(driver, "free",
					"//tbody/tr", PO_View.getTimeout());
			assertEquals(elementos.size(), 3);
		}
		
		/**
		 * Ir al formulario de alta de oferta, rellenarla con datos válidos y pulsar el botón Submit.
		 * Comprobar que la oferta sale en el listado de ofertas de dicho usuario
		 */
		@Test
		public void test14() {
			PO_NavView.clickOption(driver, "identificarse", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "isabelf@email.com" , "123456" );
			List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "mOfertasPropias", PO_View.getTimeout());
			elementos.get(0).click();
			elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "btAgregar", PO_View.getTimeout());
			elementos.get(0).click();
			PO_AddOfferView.fillForm(driver, "Producto Test", "detalles del producto", "50.00");
			SeleniumUtils.textoPresentePagina(driver, "Ofertas agregadas");
			SeleniumUtils.textoPresentePagina(driver, "Producto Test");
		}
		
		/**
		 * Ir al formulario de alta de oferta, rellenarla con datos inválidos (campo título vacío) 
		 * y pulsar el botón Submit. Comprobar que se muestra el mensaje de campo obligatorio.
		 */
		@Test
		public void test15() {
			PO_NavView.clickOption(driver, "identificarse", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "isabelf@email.com" , "123456" );
			List<WebElement> elementos = 
					SeleniumUtils.EsperaCargaPagina(driver, "id", "mOfertasPropias", PO_View.getTimeout());
			elementos.get(0).click();
			elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "btAgregar", PO_View.getTimeout());
			elementos.get(0).click();
			PO_AddOfferView.fillForm(driver, "", "detalles del producto", "50.00");
			SeleniumUtils.textoPresentePagina(driver, "Agregar oferta");
			SeleniumUtils.textoNoPresentePagina(driver, "Ofertas agregadas");
		}
		
		/**
		 * Mostrar el listado de ofertas para dicho usuario y comprobar que se muestran todas 
		 * los que existen para este usuario. 
		 */
		@Test
		public void test16() {
			PO_NavView.clickOption(driver, "identificarse", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "isabelf@email.com" , "123456" );
			List<WebElement> elementos = 
					SeleniumUtils.EsperaCargaPagina(driver, "id", "mOfertasPropias", PO_View.getTimeout());
			elementos.get(0).click();
			elementos = SeleniumUtils.EsperaCargaPagina(driver, "free",
					"//tbody/tr", PO_View.getTimeout());
			
			assertEquals(elementos.size(), 4);
		}
		
		/**
		 * Ir a la lista de ofertas, borrar la primera oferta de la lista, comprobar que la lista se actualiza y
		 * que la oferta desaparece.
		 */
		@Test
		public void test17() {
			PO_NavView.clickOption(driver, "identificarse", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "isabelf@email.com" , "123456" );
			List<WebElement> elementos = 
					SeleniumUtils.EsperaCargaPagina(driver, "id", "mOfertasPropias", PO_View.getTimeout());
			elementos.get(0).click();
			elementos = SeleniumUtils.EsperaCargaPagina(driver, "free",
					"//tbody/tr", PO_View.getTimeout());
			elementos = PO_View.checkElement(driver, "free", 
					"//td[contains(text(), 'Carpeta')]/following-sibling::*/a[contains(@href, '/propias/eliminar')]");
			elementos.get(0).click();
			SeleniumUtils.EsperaCargaPaginaNoTexto(driver, "Carpeta",PO_View.getTimeout() );
		}
		
		/**
		 * Ir a la lista de ofertas, borrar la última oferta de la lista, comprobar que la lista se actualiza y
		 * que la oferta desaparece.
		 */
		@Test
		public void test18() {
			PO_NavView.clickOption(driver, "identificarse", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "isabelf@email.com" , "123456" );
			List<WebElement> elementos = 
					SeleniumUtils.EsperaCargaPagina(driver, "id", "mOfertasPropias", PO_View.getTimeout());
			elementos.get(0).click();
			elementos = SeleniumUtils.EsperaCargaPagina(driver, "free",
					"//tbody/tr", PO_View.getTimeout());
			elementos = PO_View.checkElement(driver, "free", 
					"//td[contains(text(), 'CD')]/following-sibling::*/a[contains(@href, '/propias/eliminar')]");
			elementos.get(0).click();
			SeleniumUtils.EsperaCargaPaginaNoTexto(driver, "CD",PO_View.getTimeout() );
		}
		
		/**
		 * Hacer una búsqueda con el campo vacío y comprobar que se muestra la página que
		 * corresponde con el listado de las ofertas existentes en el sistema
		 */
		@Test
		public void test19() {
			PO_NavView.clickOption(driver, "identificarse", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "lucasr@email.com" , "123456" );
			List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, "free",
					"//tbody/tr", PO_View.getTimeout()); 
			assertEquals(elementos.size(), 4);
			SeleniumUtils.textoPresentePagina(driver, "Carpeta");
			SeleniumUtils.textoPresentePagina(driver, "Altavoz");
			SeleniumUtils.textoPresentePagina(driver, "Lápices");
			SeleniumUtils.textoPresentePagina(driver, "CD");
			WebElement busqueda = driver.findElement(By.name("busqueda"));
			busqueda.click();
			busqueda.clear();
			busqueda.sendKeys("");
			//Pulsar el boton de Alta.
			By boton = By.className("btn");
			driver.findElement(boton).click();
			elementos = SeleniumUtils.EsperaCargaPagina(driver, "free",
					"//tbody/tr", PO_View.getTimeout()); 
			// Comprobamos que se muestran los 4 elementos de la primera página que había antes
			assertEquals(elementos.size(), 4);
			SeleniumUtils.textoPresentePagina(driver, "Carpeta");
			SeleniumUtils.textoPresentePagina(driver, "Altavoz");
			SeleniumUtils.textoPresentePagina(driver, "Lápices");
			SeleniumUtils.textoPresentePagina(driver, "CD");
		}
		
		/**
		 * Hacer una búsqueda escribiendo en el campo un texto que no exista y comprobar que se
		 * muestra la página que corresponde, con la lista de ofertas vacía.
		 */
		@Test
		public void test20() {
			PO_NavView.clickOption(driver, "identificarse", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "lucasr@email.com" , "123456" );
			WebElement busqueda = driver.findElement(By.name("busqueda"));
			busqueda.click();
			busqueda.clear();
			busqueda.sendKeys("Armario");
			//Pulsar el boton de Alta.
			By boton = By.className("btn");
			driver.findElement(boton).click();
			SeleniumUtils.textoNoPresentePagina(driver, "Armario");
		}
		
		/**
		 * Hacer una búsqueda escribiendo en el campo un texto en minúscula o mayúscula y
		 * comprobar que se muestra la página que corresponde, con la lista de ofertas que contengan dicho texto,
		 * independientemente que el título esté almacenado en minúsculas o mayúscula.
		 */
		@Test
		public void test21() {
			PO_NavView.clickOption(driver, "identificarse", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "lucasr@email.com" , "123456" );
			WebElement busqueda = driver.findElement(By.name("busqueda"));
			busqueda.click();
			busqueda.clear();
			busqueda.sendKeys("libro");
			//Pulsar el boton de Alta.
			By boton = By.className("btn");
			driver.findElement(boton).click();
			List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, "free",
					"//tbody/tr", PO_View.getTimeout()); 
			SeleniumUtils.textoPresentePagina(driver, "Libro");
			assertEquals(elementos.size(), 2);
		}
		
		/**
		 * Sobre una búsqueda determinada (a elección de desarrollador), comprar una oferta que deja
		 * un saldo positivo en el contador del comprobador. Y comprobar que el contador se actualiza
		 * correctamente en la vista del comprador.
		 */
		@Test
		public void test22() {
			PO_NavView.clickOption(driver, "identificarse", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "lucasr@email.com" , "123456" );
			SeleniumUtils.textoPresentePagina(driver, "lucasr@email.com : 70 €");
			WebElement busqueda = driver.findElement(By.name("busqueda"));
			busqueda.click();
			busqueda.clear();
			busqueda.sendKeys("carpeta");
			//Pulsar el boton de Alta.
			By boton = By.className("btn");
			driver.findElement(boton).click();
			List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, "free",
					"//tbody/tr", PO_View.getTimeout()); 	
			elementos = PO_View.checkElement(driver, "free", 
					"//td[contains(text(), 'Carpeta')]/following-sibling::*/a[contains(@href, '/oferta/comprar')]");
			elementos.get(0).click();
			SeleniumUtils.textoPresentePagina(driver, "lucasr@email.com : 65 €");
		}
		
		/**
		 * Sobre una búsqueda determinada (a elección de desarrollador), comprar una oferta que deja
		 * un saldo 0 en el contador del comprobador. Y comprobar que el contador se actualiza correctamente en
	  	 * la vista del comprador.  
	  	 * */
		@Test
		public void test23() {
			PO_NavView.clickOption(driver, "identificarse", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "lucasr@email.com" , "123456" );
			SeleniumUtils.textoPresentePagina(driver, "lucasr@email.com : 70 €");
			WebElement busqueda = driver.findElement(By.name("busqueda"));
			busqueda.click();
			busqueda.clear();
			busqueda.sendKeys("monitor");
			//Pulsar el boton de Alta.
			By boton = By.className("btn");
			driver.findElement(boton).click();
			List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, "free",
					"//tbody/tr", PO_View.getTimeout()); 	
			elementos = PO_View.checkElement(driver, "free", 
					"//td[contains(text(), 'Monitor')]/following-sibling::*/a[contains(@href, '/oferta/comprar')]");
			elementos.get(0).click();
			SeleniumUtils.textoPresentePagina(driver, "lucasr@email.com : 0 €");
		}
		
		/**
		 * Sobre una búsqueda determinada (a elección de desarrollador), intentar comprar una oferta
		 * que esté por encima de saldo disponible del comprador. Y comprobar que se muestra el mensaje de
		 * saldo no suficiente. 
		 * */
		@Test
		public void test24() {
			PO_NavView.clickOption(driver, "identificarse", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "mariag@email.com" , "123456" );
			SeleniumUtils.textoPresentePagina(driver, "mariag@email.com : 60 €");
			WebElement busqueda = driver.findElement(By.name("busqueda"));
			busqueda.click();
			busqueda.clear();
			busqueda.sendKeys("monitor");
			//Pulsar el boton de Alta.
			By boton = By.className("btn");
			driver.findElement(boton).click();
			List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, "free",
					"//tbody/tr", PO_View.getTimeout()); 	
			elementos = PO_View.checkElement(driver, "free", 
					"//td[contains(text(), 'Monitor')]/following-sibling::*/a[contains(@href, '/oferta/comprar')]");
			elementos.get(0).click();
			SeleniumUtils.textoPresentePagina(driver, "Saldo insuficiente");
		}
		
		/**
		 * Ir a la opción de ofertas compradas del usuario y mostrar la lista. Comprobar que aparecen
		 * las ofertas que deben aparecer.
		 * */
		@Test
		public void test25() {
			PO_NavView.clickOption(driver, "identificarse", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "lucasr@email.com" , "123456" );
			List<WebElement> elementos = 
					SeleniumUtils.EsperaCargaPagina(driver, "id", "mCompras", PO_View.getTimeout());
			elementos.get(0).click();
			elementos = SeleniumUtils.EsperaCargaPagina(driver, "free",
					"//tbody/tr", PO_View.getTimeout()); 
			assertEquals(elementos.size(), 2);
			SeleniumUtils.textoPresentePagina(driver, "El código da Vinci");
			SeleniumUtils.textoPresentePagina(driver, "Libros de ESDLA");
		}
		
		/**
		 * Inicio de sesión con datos válidos.
		 */
		@Test
		public void test29() {
			PO_NavView.clickOption(driver, "identificarse", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "isabelf@email.com" , "123456" );
			List<WebElement> elementos = 
					SeleniumUtils.EsperaCargaPagina(driver, "id", "mChats", PO_View.getTimeout());
			elementos.get(0).click();
			PO_LoginView.fillForm(driver, "isabelf@email.com" , "123456" );
			SeleniumUtils.textoPresentePagina(driver, "Agenda escolar");
		}
		
		/**
		 * Inicio de sesión con datos inválidos (email existente, pero contraseña incorrecta).
		 */
		@Test
		public void test30() {
			PO_NavView.clickOption(driver, "identificarse", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "isabelf@email.com" , "123456" );
			List<WebElement> elementos = 
					SeleniumUtils.EsperaCargaPagina(driver, "id", "mChats", PO_View.getTimeout());
			elementos.get(0).click();
			PO_LoginView.fillForm(driver, "isabelf@email.com" , "12" );
			SeleniumUtils.textoPresentePagina(driver, "Usuario no encontrado");
			SeleniumUtils.textoNoPresentePagina(driver, "Listado de ofertas");
		}
		
		/**
		 * Inicio de sesión con datos válidos (email vacío).
		 */
		@Test
		public void test31_1() {
			PO_NavView.clickOption(driver, "identificarse", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "isabelf@email.com" , "123456" );
			List<WebElement> elementos = 
					SeleniumUtils.EsperaCargaPagina(driver, "id", "mChats", PO_View.getTimeout());
			elementos.get(0).click();
			PO_LoginView.fillForm(driver, "" , "123456" );
			SeleniumUtils.textoPresentePagina(driver, "Usuario no encontrado");
			SeleniumUtils.textoNoPresentePagina(driver, "Listado de ofertas");
		}
		
		/**
		 * Inicio de sesión con datos válidos (contraseña vacía).
		 */
		@Test
		public void test31_2() {
			PO_NavView.clickOption(driver, "identificarse", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "isabelf@email.com" , "123456" );
			List<WebElement> elementos = 
					SeleniumUtils.EsperaCargaPagina(driver, "id", "mChats", PO_View.getTimeout());
			elementos.get(0).click();
			PO_LoginView.fillForm(driver, "isabelf@email.com" , "" );
			SeleniumUtils.textoPresentePagina(driver, "Usuario no encontrado");
		}
		
		/**
		 * Mostrar el listado de ofertas disponibles y comprobar que se muestran 
		 * todas las que existen, menos las del usuario identificado.
		 */
		@Test
		public void test32() {
			PO_NavView.clickOption(driver, "identificarse", "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "isabelf@email.com" , "123456" );
			List<WebElement> elementos = 
					SeleniumUtils.EsperaCargaPagina(driver, "id", "mChats", PO_View.getTimeout());
			elementos.get(0).click();
			PO_LoginView.fillForm(driver, "isabelf@email.com" , "123456" );
			elementos = SeleniumUtils.EsperaCargaPagina(driver, "free",
					"//tbody/tr", PO_View.getTimeout()); 	
			assertEquals(elementos.size(), 8);
		}

}
