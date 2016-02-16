Feature: Iridum Web Service
  To allow testing of SOAP API

  Scenario Outline: findServiceProviderProfile test scenario
    Given that the Iridiums service is up and running
    And a request is sent to findServiceProviderProfile with iwsUsername '<iwsUsername>', signature '<signature>', serviceProviderAccountNumber '<serviceProviderAccountNumber>' and timestamp '<timestamp>'
    Then the result must contain the expected account number '<serviceProviderAccountNumber>' and account name '<accountName>' 
   
	 Examples:
 | iwsUsername | signature | serviceProviderAccountNumber | timestamp | accountName | 
 | IWSTESTSP0001 | H5px/4kyHgDxqtiC9Cws9/Jc85I= | 200367 | 2016-02-16T11:35:24Z | INOV INESC Inovacao - Instituto de Novas Tecnologias | 
 | IWSTESTSP0001 | pPrB3/AhVr9+pU/XLgeHesbNc6c= | 100045 | 2016-02-16T11:35:26Z | SATCOM DIRECT, INC. (HW) | 
 | IWSTESTSP0001 | Luaj3K19SV/MjU8rarh3HlrbdDs= | 101237 | 2016-02-16T11:35:27Z | Globalsat Group LLC | 
 | IWSTESTSP0001 | f6xtrc1BQSwwlePla4dme/O9aZw= | 101076 | 2016-02-16T11:35:28Z | PT Amalgam | 
 | IWSTESTSP0001 | WPo0WDNxrhGk2Sts+u9r3MDbobA= | 200483 | 2016-02-16T11:35:29Z | Rastrack Satelital Ecuarastrack S.A. | 
 | IWSTESTSP0001 | tJWw9MoDXnk5YMrj5CU8wrxF9iY= | 200367 | 2016-02-16T11:35:30Z | INOV INESC Inovacao - Instituto de Novas Tecnologias | 
 | IWSTESTSP0001 | r1oEtjQwTMzYsgZ1tzpdbQC5ck0= | 101098 | 2016-02-16T11:35:31Z | CLS | 






    
