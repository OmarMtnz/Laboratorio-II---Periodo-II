from fastapi import FastAPI, HTTPException, Path
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, EmailStr
from typing import Optional
from sqlalchemy import create_engine, Column, Integer, String
from sqlalchemy.orm import declarative_base, sessionmaker

# Ajusta tu cadena de conexión (sin tildes/ñ) o usa URL encoding
DATABASE_URL = "postgresql://omarmtnz:9ByOEJ5Jc7jEIDJH1ZewApueTK6ragtr@dpg-d3be7nodl3ps739an4p0-a/mm100721_g8lt"

engine = create_engine(DATABASE_URL)
SessionLocal = sessionmaker(bind=engine, autoflush=False, autocommit=False)
Base = declarative_base()

# MODELO DE BD
class Estudiante(Base):
    __tablename__ = "estudiantes"
    id = Column(Integer, primary_key=True, index=True)
    nombre = Column(String(100), nullable=False, index=True)
    edad = Column(Integer, nullable=False)
    correo = Column(String(120), nullable=False, unique=True, index=True)
    carnet = Column(String(20), nullable=False, unique=True, index=True)
    pic_url = Column(String(500), nullable=True, index=True)

Base.metadata.create_all(bind=engine)

# SCHEMA (entrada/salida)
class EstudianteSchema(BaseModel):
    nombre: str
    edad: int
    correo: EmailStr
    carnet: str
    pic_url: Optional[str] = None

app = FastAPI()

# CORS (para que la app Android pueda llamar sin problemas)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # en producción restringe dominios
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# POST
@app.post("/estudiantes/")
def insertar_estudiante(estudiante: EstudianteSchema):
    db = SessionLocal()
    try:
        nuevo = Estudiante(
            nombre=estudiante.nombre,
            edad=estudiante.edad,
            correo=estudiante.correo,
            carnet=estudiante.carnet,
            pic_url=estudiante.pic_url,
        )
        db.add(nuevo)
        db.commit()
        db.refresh(nuevo)
        return {"mensaje": "Estudiante insertado", "data": nuevo.id}
    finally:
        db.close()

# GET
@app.get("/estudiantes/")
def listar_estudiantes():
    db = SessionLocal()
    try:
        return db.query(Estudiante).all()
    finally:
        db.close()

# PUT
@app.put("/estudiantes/{id}")
def actualizar_estudiante(
    id: int = Path(..., description="ID del estudiante a actualizar"),
    estudiante: EstudianteSchema = None
):
    db = SessionLocal()
    try:
        est = db.query(Estudiante).filter(Estudiante.id == id).first()
        if not est:
            raise HTTPException(status_code=404, detail="Estudiante no encontrado")
        est.nombre = estudiante.nombre
        est.edad = estudiante.edad
        est.correo = estudiante.correo
        est.carnet = estudiante.carnet
        est.pic_url = estudiante.pic_url

        if estudiante.pic_url is not None:
            est.pic_url = estudiante.pic_url

        db.commit()
        db.refresh(est)
        return {
            "mensaje": "Estudiante actualizado",
            "data": {
                "id": est.id,
                "nombre": est.nombre,
                "edad": est.edad,
                "correo": est.correo,
                "carnet": est.carnet,
                "pic_url": est.pic_url,
            }
        }
    finally:
        db.close()

# DELETE
@app.delete("/estudiantes/{id}")
def eliminar_estudiante(id: int):
    db = SessionLocal()
    try:
        est = db.query(Estudiante).filter(Estudiante.id == id).first()
        if not est:
            raise HTTPException(status_code=404, detail="No encontrado")
        db.delete(est)
        db.commit()
        return {"mensaje": "Estudiante eliminado"}
    finally:
        db.close()
