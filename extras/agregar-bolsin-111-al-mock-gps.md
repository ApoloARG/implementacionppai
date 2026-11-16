# (Opcional) Agregar el bolsín 111 al mock del GPS de la cátedra

Con los datos tal cual vienen en `dsi-api-bolsines`, la CM Córdoba (CM-CBA-01) tiene
un solo bolsín conocido por el GPS (el 101). Si querés que en la defensa se vean **dos
marcadores** en el mapa y que siga apareciendo el flujo **A3** (bolsín 112 sin ubicación),
agregá esta línea al final del método `init()` de:

`dsi-api-bolsines/src/main/java/ar/edu/dsi/gpstracker/repository/InMemoryBolsinRepository.java`

```java
bolsines.add(new Bolsin(111, "CM-CBA-01", "CM-MDZ-03", -33.2950, -66.3356, LocalDateTime.of(2026, 3, 7, 12, 55, 10)));
```

El README de la API indica que ese es el lugar para cambiar los datos mock.
(Sin este cambio todo funciona igual: el 111 aparece en la lista de "sin ubicación".)
