package test.pageobjects;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PO_AddOfferView  extends PO_NavView {
	
	static public void fillForm(WebDriver driver, String description, String details, String
			price) {
		WebElement descriptionw = driver.findElement(By.name("nombre"));
		descriptionw.click();
		descriptionw.clear();
		descriptionw.sendKeys(description);
		WebElement detailsw = driver.findElement(By.name("descripcion"));
		detailsw.click();
		detailsw.clear();
		detailsw.sendKeys(details);
		WebElement pricew = driver.findElement(By.name("precio"));
		pricew.click();
		pricew.clear();
		pricew.sendKeys(price);

		//Pulsar el boton de Alta.
		By boton = By.className("btn");
		driver.findElement(boton).click();
	}
}
