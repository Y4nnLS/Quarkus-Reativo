# API de Prêmios de Filmes

Este projeto é uma API RESTful para gerenciar e recuperar dados de filmes, construída usando Quarkus com Hibernate ORM e Panache. Ele fornece endpoints para realizar operações CRUD em filmes, criação em massa a partir de um arquivo CSV e cálculo de intervalos de prêmios para produtores de filmes.

## Funcionalidades

- Operações CRUD para filmes.
- Criação em massa de filmes a partir de um arquivo CSV.
- Cálculo dos intervalos mais curtos e mais longos de prêmios para produtores de filmes.

## Endpoints

### Obter Todos os Filmes

```
GET /movies
```
Retorna uma lista de todos os filmes.

### Obter um Filme

```
GET /movies/{id}
```
Retorna um filme pelo seu ID.

### Criar Filme

```
POST /movies
```
Cria um novo filme. O corpo da requisição deve conter os dados do filme em formato JSON, sem o campo `id` (ele será gerado automaticamente).

Exemplo de corpo de requisição:
```json
{
    "title": "Título do Filme",
    "producers": "Nome do Produtor",
    "studios": "Nome do Estúdio",
    "releaseYear": 2023,
    "winner": false
}
```

### Atualizar Filme

```
PUT /movies/{id}
```
Atualiza os dados de um filme existente pelo seu ID. O corpo da requisição deve conter os dados atualizados do filme em formato JSON.

Exemplo de corpo de requisição:
```json
{
    "title": "Novo Título do Filme"
}
```

### Deletar Filme

```
DELETE /movies/{id}
```
Remove um filme pelo seu ID.

### Criação em Massa de Filmes a Partir de um Arquivo CSV

```
POST /movies/bulk
```
Lê um arquivo CSV localizado em `src/main/java/org/acme/hibernate/orm/panache/movielist.csv` e cria filmes com base nos dados do arquivo. 

### Carregar Filmes do CSV

```
GET /movies/load-csv
```
Lê e carrega filmes do arquivo CSV.

### Calcular Intervalos de Prêmios

```
GET /movies/awardInterval
```
Calcula os intervalos mais curtos e mais longos entre prêmios para produtores de filmes.

## Tratamento de Erros

Erros são tratados e mapeados para respostas JSON pelo `ErrorMapper`. Em caso de erro, a resposta terá o seguinte formato:

```json
{
    "exceptionType": "TipoDaExceção",
    "code": 500,
    "error": "Mensagem de Erro"
}
```

## Estrutura do Projeto

- `MovieResource.java`: Contém todos os endpoints da API.
- `Movie.java`: Entidade representando um filme.
- `ErrorMapper.java`: Mapeia exceções para respostas HTTP adequadas.

## Como Executar

1. Clone o repositório:
    ```bash
    git clone <URL-do-repositório>
    ```
2. Navegue até o diretório do projeto:
    ```bash
    cd hibernate-reactive-panache-quickstart
    ```
3. Execute o projeto com o Quarkus:
    ```bash
    ./mvnw quarkus:dev
    ```

## Dependências

- Quarkus
- Hibernate ORM com Panache
- Jackson (para serialização JSON)
- SmallRye Mutiny (para programação reativa)

## Autor

- Nome do Autor

---

Este README fornece uma visão geral das funcionalidades e da estrutura do projeto. Para mais detalhes, consulte o código-fonte e a documentação do Quarkus.