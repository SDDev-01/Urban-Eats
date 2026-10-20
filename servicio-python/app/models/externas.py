"""Referencias minimas a tablas que pertenecen a Spring Boot.

Solo se declara la llave primaria para que las llaves foraneas de las tablas
de Python (por ejemplo solicitud_restaurante.CodigoUsuario -> usuario) se puedan
resolver. create_all() nunca las crea porque ya existen en la base.
Para leer o escribir datos de estas tablas el servicio usa SQL explicito (text()).
"""
from sqlalchemy import Column, Integer, Table

from app.core.database import Base

usuario = Table("usuario", Base.metadata, Column("CodigoUsuario", Integer, primary_key=True))
restaurante = Table("restaurante", Base.metadata, Column("CodigoRestaurante", Integer, primary_key=True))
