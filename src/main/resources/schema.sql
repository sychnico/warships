CREATE DATABASE IF NOT EXISTS warships_db;
GRANT ALL PRIVILEGES ON DATABASE warships_db TO postgres;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO postgres;

CREATE TABLE gamers (
    id BIGSERIAL PRIMARY KEY,
    nickname VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE game_sessions (
    id BIGSERIAL PRIMARY KEY,
    player_one_id BIGINT NOT NULL,
    player_two_id BIGINT,
    status VARCHAR(50) NOT NULL,
    type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    finished_at TIMESTAMP,
    winner_id BIGINT,
    CONSTRAINT fk_player_one FOREIGN KEY (player_one_id) REFERENCES gamers(id),
    CONSTRAINT fk_player_two FOREIGN KEY (player_two_id) REFERENCES gamers(id),
    CONSTRAINT fk_winner FOREIGN KEY (winner_id) REFERENCES gamers(id)
);

CREATE TABLE gaming_fields (
    id BIGSERIAL PRIMARY KEY,
    gamer_id BIGINT NOT NULL,
    game_session_id BIGINT NOT NULL,
    field_data TEXT NOT NULL,
    CONSTRAINT fk_gamer FOREIGN KEY (gamer_id) REFERENCES gamers(id),
    CONSTRAINT fk_game_session FOREIGN KEY (game_session_id) REFERENCES game_sessions(id)
);
