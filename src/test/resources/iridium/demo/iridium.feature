Feature: Iridum Web Service
  To allow testing of SOAP API

  Scenario Outline: findServiceProviderProfile test scenario
    Given that the Iridiums service is up and running
    And a request is sent to findServiceProviderProfile with iwsUsername '<iwsUsername>', signature '<signature>', serviceProviderAccountNumber '<serviceProviderAccountNumber>' and timestamp '<timestamp>'
    Then the result must contain the expected account number '<serviceProviderAccountNumber>' and account name '<accountName>' 
    
	 Examples:
 | iwsUsername | signature | serviceProviderAccountNumber | timestamp | accountName | 
 | IWSTESTSP0001 | rNEUrPuag68wwJOnZhe+h/d0leo= | 101222 | 2016-02-10T17:26:17Z | MVS Latam | 
 | IWSTESTSP0001 | UBIhTALHED8V3gHDruuLenjaeLs= | 101196 | 2016-02-10T17:26:18Z | SkyBitz Inc | 
 | IWSTESTSP0001 | /n51FndLbPLMGUH614ZljogZy/8= | 200487 | 2016-02-10T17:26:19Z | Airbus Defence and Space (Norway MOD) | 
 | IWSTESTSP0001 | Dx3rw6a7zQ9hUqNrmpLF5EUGuFg= | 101200 | 2016-02-10T17:26:21Z | Mawarid | 
 | IWSTESTSP0001 | AETnvphDzlkjugym9+LQL4zZu3g= | 101172 | 2016-02-10T17:26:22Z | OHB TECHNOLOGY AG | 
 | IWSTESTSP0001 | wevd5T+vuvrRaCRHa9xIAo+v9fU= | 400003 | 2016-02-10T17:26:23Z | SBD DEMO - Iridium Prd Eng | 
 | IWSTESTSP0001 | gyYhH7E3fONNAPHtW8lfEK73RZk= | 101202 | 2016-02-10T17:26:24Z | Field Intelligence Inc. | 





    
