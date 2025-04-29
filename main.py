from fastapi import FastAPI, UploadFile, File, Form, HTTPException
from fastapi.responses import FileResponse, HTMLResponse, RedirectResponse
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles
from fastapi.responses import JSONResponse
from database import SessionLocal, engine
from models import Base, Song
from fastapi import FastAPI, Depends
from sqlalchemy.orm import Session
from fastapi.middleware.cors import CORSMiddleware
from database import get_db, engine
import shutil
import os
from pathlib import Path
# main.py
from fastapi import FastAPI

app = FastAPI()

# Créer les tables SQL si elles n'existent pas
Base.metadata.create_all(bind=engine)
from models import Favorite  # déjà inclus par Base, mais à garder en tête



# CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Répertoire d'upload
UPLOAD_DIR = "uploads"
os.makedirs(UPLOAD_DIR, exist_ok=True)

# Servir les fichiers MP3
app.mount("/song_files", StaticFiles(directory=UPLOAD_DIR), name="song_files")

def check_existing_song(title: str, artist: str, filename: str):
    db = SessionLocal()
    try:
        # Vérifier si une chanson identique existe déjà
        existing = db.query(Song).filter(
            Song.title == title,
            Song.artist == artist,
            Song.filename == filename
        ).first()
        
        return existing is not None
    finally:
        db.close()

def remove_duplicate_songs(title: str, artist: str, filename: str):
    db = SessionLocal()
    try:
        # Trouver et supprimer les doublons
        duplicates = db.query(Song).filter(
            Song.title == title,
            Song.artist == artist,
            Song.filename == filename
        ).all()
        
        if len(duplicates) > 1:
            # Garder le premier et supprimer les autres
            for duplicate in duplicates[1:]:
                file_path = os.path.join(UPLOAD_DIR, duplicate.filename)
                if os.path.exists(file_path):
                    os.remove(file_path)
                db.delete(duplicate)
            db.commit()
    finally:
        db.close()

# Page d'accueil
@app.get("/", response_class=HTMLResponse)
async def root():
    return """
    <h2>Uploader une chanson</h2>
    <form action="/upload/" enctype="multipart/form-data" method="post">
      Titre: <input name="title" type="text" required><br>
      Artiste: <input name="artist" type="text" required><br>
      Fichier MP3: <input name="file" type="file" accept=".mp3" required><br><br>
      <input type="submit" value="Uploader">
    </form>
    <p><a href="/list_songs">Voir la liste des chansons</a></p>
    """

# Upload de chanson
@app.post("/upload/")
async def upload_song(
    file: UploadFile = File(...),
    title: str = Form(...),
    artist: str = Form(...)
):
    # Vérifier l'extension du fichier
    if not file.filename.lower().endswith('.mp3'):
        raise HTTPException(status_code=400, detail="Seuls les fichiers MP3 sont acceptés")

    # Vérifier si la chanson existe déjà
    if check_existing_song(title, artist, file.filename):
        raise HTTPException(
            status_code=400,
            detail="Cette chanson existe déjà (même titre, artiste et fichier)"
        )

    # Enregistrer le fichier
    file_path = os.path.join(UPLOAD_DIR, file.filename)
    
    try:
        with open(file_path, "wb") as buffer:
            shutil.copyfileobj(file.file, buffer)
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erreur lors de l'enregistrement du fichier: {str(e)}")

    # Ajouter à la base de données
    db = SessionLocal()
    try:
        song = Song(title=title, artist=artist, filename=file.filename)
        db.add(song)
        db.commit()
        db.refresh(song)
        
        # Supprimer les doublons éventuels
        remove_duplicate_songs(title, artist, file.filename)
        
        return RedirectResponse(url="/list_songs", status_code=303)
    except Exception as e:
        db.rollback()
        # Supprimer le fichier si l'insertion en base a échoué
        if os.path.exists(file_path):
            os.remove(file_path)
        raise HTTPException(status_code=500, detail=f"Erreur lors de l'ajout à la base de données: {str(e)}")
    finally:
        db.close()

# Liste des chansons
@app.get("/list_songs", response_class=HTMLResponse)
async def list_songs():
    db = SessionLocal()
    try:
        songs = db.query(Song).all()
        
        # Générer le contenu HTML dynamiquement
        songs_html = """
        <html>
        <head>
            <title>Liste des chansons</title>
            <style>
                body { font-family: Arial, sans-serif; margin: 20px; }
                ul { list-style-type: none; padding: 0; }
                li { padding: 8px; margin-bottom: 5px; background: #f0f0f0; border-radius: 4px; }
                a { color: #0066cc; text-decoration: none; }
                a:hover { text-decoration: underline; }
                .error { color: red; }
            </style>
        </head>
        <body>
            <h2>Liste des chansons</h2>
            <ul>
        """
        
        for song in songs:
            songs_html += f"""
            <li>
                <strong>{song.title}</strong> - {song.artist}
                <a href="/song_files/{song.filename}" target="_blank">Écouter</a>
                <a href="/song_info/{song.id}">Détails</a>
                <form action="/delete_song/{song.id}" method="post" style="display: inline;">
                    <button type="submit" style="color: red; margin-left: 10px;">Supprimer</button>
                </form>
            </li>
            """
        
        songs_html += """
            </ul>
            <p><a href="/">Retour à l'accueil</a></p>
        </body>
        </html>
        """
        
        return HTMLResponse(content=songs_html)
    finally:
        db.close()

# Détails d'une chanson
@app.get("/song_info/{song_id}", response_class=HTMLResponse)
async def song_info(song_id: int):
    db = SessionLocal()
    try:
        song = db.query(Song).filter(Song.id == song_id).first()
        
        if not song:
            raise HTTPException(status_code=404, detail="Chanson non trouvée")
        
        return f"""
        <html>
        <head>
            <title>Détails de {song.title}</title>
            <style>
                body {{ font-family: Arial, sans-serif; margin: 20px; }}
                audio {{ width: 100%; margin: 20px 0; }}
                a {{ color: #0066cc; text-decoration: none; }}
                a:hover {{ text-decoration: underline; }}
            </style>
        </head>
        <body>
            <h2>Détails de la chanson</h2>
            <p><strong>Titre:</strong> {song.title}</p>
            <p><strong>Artiste:</strong> {song.artist}</p>
            <p><strong>Fichier:</strong> {song.filename}</p>
            
            <audio controls>
                <source src="/song_files/{song.filename}" type="audio/mpeg">
                Votre navigateur ne supporte pas l'élément audio.
            </audio>
            
            <p>
                <a href="/list_songs">Retour à la liste</a> | 
                <a href="/">Accueil</a>
            </p>
        </body>
        </html>
        """
    finally:
        db.close()

# Supprimer une chanson
@app.post("/delete_song/{song_id}")
async def delete_song(song_id: int):
    db = SessionLocal()
    try:
        song = db.query(Song).filter(Song.id == song_id).first()
        
        if not song:
            raise HTTPException(status_code=404, detail="Chanson non trouvée")
        
        file_path = os.path.join(UPLOAD_DIR, song.filename)
        
        # Supprimer le fichier
        if os.path.exists(file_path):
            os.remove(file_path)
        
        # Supprimer de la base de données
        db.delete(song)
        db.commit()
        
        return RedirectResponse(url="/list_songs", status_code=303)
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=500, detail=f"Erreur lors de la suppression: {str(e)}")
    finally:
        db.close()
@app.get("/api/songs")
async def api_songs():
    db = SessionLocal()
    try:
        songs = db.query(Song).all()
        return JSONResponse([{
            "id": song.id,
            "title": song.title,
            "artist": song.artist,
            "filename": song.filename
        } for song in songs])
    finally:
        db.close()
@app.post("/api/favorites/{song_id}")
async def add_favorite(song_id: int, db: Session = Depends(get_db)):
    # Vérifie si la chanson existe
    song = db.query(Song).filter(Song.id == song_id).first()
    if not song:
        raise HTTPException(status_code=404, detail="Chanson non trouvée")

    # Vérifie si elle est déjà en favoris
    existing = db.query(Favorite).filter(Favorite.song_id == song_id).first()
    if existing:
        return {"message": "Déjà en favoris"}

    favorite = Favorite(song_id=song_id)
    db.add(favorite)
    db.commit()
    return {"message": "Ajouté aux favoris"}
@app.get("/api/favorites")
async def get_favorites(db: Session = Depends(get_db)):
    favorites = db.query(Favorite).all()
    return JSONResponse([{
        "id": fav.song.id,
        "title": fav.song.title,
        "artist": fav.song.artist,
        "filename": fav.song.filename
    } for fav in favorites])
@app.delete("/api/favorites/{song_id}")
async def remove_favorite(song_id: int, db: Session = Depends(get_db)):
    favorite = db.query(Favorite).filter(Favorite.song_id == song_id).first()
    if not favorite:
        raise HTTPException(status_code=404, detail="Pas en favoris")
    
    db.delete(favorite)
    db.commit()
    return {"message": "Supprimé des favoris"}



if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)