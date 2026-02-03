# Manual do SpawnerX

Este documento fornece as instruções para instalar e compilar o plugin SpawnerX.

---

## Instalação

Para instalar o plugin SpawnerX em seu servidor Bukkit/Paper, siga estes passos:

1.  **Baixe o arquivo**: Faça o download do arquivo `SpawnerX.jar`.
2.  **Mova o plugin**: Coloque o arquivo `SpawnerX.jar` na pasta `plugins/` do seu servidor.
3.  **Inicie o servidor**: Inicie ou reinicie seu servidor. O plugin criará sua pasta de configuração (`plugins/SpawnerX/`) na primeira vez que for executado.
4.  **Configure (Opcional)**: Edite os arquivos `config.yml` e `locale/pt_BR.yml` ou `locale/en_US.yml` dentro da pasta `plugins/SpawnerX/` para ajustar as funcionalidades conforme sua necessidade. Após editar, você pode usar o comando `/spawnerx reload` no jogo para aplicar as mudanças sem reiniciar o servidor.

---

## Compilação (Build)

Se você deseja modificar o código-fonte e compilar sua própria versão do plugin, siga estas instruções.

### Pré-requisitos

*   **Java Development Kit (JDK)**: Versão 21 ou superior.
*   **Apache Maven**: Versão 3.6 ou superior.
*   **Git**: Para clonar o repositório do código-fonte.

### Passos para Compilar

1.  **Clone o repositório**: Abra um terminal ou prompt de comando e clone o código-fonte do projeto:
    ```bash
    # (Exemplo, substitua pela URL correta do repositório quando disponível)
    git clone https://github.com/yeyTaken/SpawnerX.git
    cd SpawnerX
    ```

2.  **Execute o Build com Maven**: Dentro do diretório raiz do projeto (onde o arquivo `pom.xml` está localizado), execute o seguinte comando:
    ```bash
    mvn clean package
    ```

3.  **Localize o arquivo JAR**: Após a conclusão bem-sucedida do processo, o arquivo compilado do plugin, `SpawnerX-1.0.0.jar`, estará localizado na pasta `target/`.

---

**Fim do manual.**
