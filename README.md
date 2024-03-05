```sh
post /auth
post /registration
post /registrationFull - не надо было, но где брать данные для поиска по ФИО и проверять верифицированные?

//требуется jwt
post /v1/account/transfer

post 	/v1/contactInfo/create
put 	/v1/contactInfo/{id}
delete 	/v1/contactInfo/{id}

get /v1/clients/search
?search=2009-02-20		-больше даты 
?search=7(999)1111111		-100% по телефону
?search=email1@example.ru	-100% по почте
?search=Дмитриев		-like по concat ФИО
сортировка через @Pageable
```

```sh
Пару комментариев

Функциональные требования
=2=
post /registration
*регистрируются пользователи согласно п.1 без ФИО (нет функционала изменять/дополнять ФИО и переводить статус в подтвержденный)
post /registrationFull - в ресурсах responseBody-> requestRegisterFull.txt

=4-6
post 	/v1/contactInfo/create
delete 	/v1/contactInfo/{id}

=7=
@requestParam search - не может быть null
Не понятно, какую информацию должен возвращать (здесь возвращается только логин и ид)

a) больше даты, сортировка по id asc, страница -1я, строк по 10 на странице
get /v1/clients/search?search=2009-02-20&page=1&size=10&sort=id,asc
 
b) полное совпадение по телефону, максимум 1
get /v1/clients/search?search=7(999)1111111

c) частичное совпадание concat ФИО, сортировка по id asc и по firstName asc, страница -2я, строк по 3 на странице
get /v1/clients/search?search=Дмитриев&page=2&size=3&firstName=id,desc&sort=id,asc

d) полное совпадение по почте, максимум 1
get /v1/clients/search?search=email1@example.ru

=8=
авторизация /v1/** по токену

=10=
ограничение только для верифицированных пользователей (созданные пользователи через /registration - не могут делать переводы)
по почте или номеру телефону
post /v1/account/transfer
```

