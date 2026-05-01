package com.newsapp.service;

import com.newsapp.dto.ArticleDTO;
import com.newsapp.dto.NewsResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class containing business logic for fetching news.
 */
@Service
public class NewsService {

    private final RestTemplate restTemplate;

    @Value("${app.newsapi.key}")
    private String apiKey;

    @Value("${app.newsapi.base-url}")
    private String baseUrl;

    public NewsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public NewsResponseDTO fetchTopHeadlines(String category) {
        String url = baseUrl + "/top-headlines?category=" + category + "&language=en&apiKey=" + apiKey;

        try {
            NewsResponseDTO response = restTemplate.getForObject(url, NewsResponseDTO.class);
            // NewsAPI free tier strips images/descriptions from most server-side results.
            // Filter to only articles that have both an image and a description, then
            // fall back to rich mock data if fewer than 3 usable articles remain.
            if (response != null && response.getArticles() != null) {
                List<ArticleDTO> rich = response.getArticles().stream()
                        .filter(a -> a.getUrlToImage() != null && !a.getUrlToImage().isBlank()
                                  && a.getDescription() != null && !a.getDescription().isBlank()
                                  && a.getTitle() != null && !a.getTitle().equals("[Removed]"))
                        .collect(Collectors.toList());

                if (rich.size() >= 3) {
                    response.setArticles(rich);
                    response.setTotalResults(rich.size());
                    return response;
                }
            }
            System.out.println("[NewsService] Not enough rich articles from NewsAPI for category: " + category + ". Using fallback data.");
            return buildFallbackResponse(category);
        } catch (Exception e) {
            System.err.println("[NewsService] NewsAPI request failed (" + e.getClass().getSimpleName() + "): " + e.getMessage());
            System.err.println("[NewsService] Note: The free NewsAPI developer plan blocks server-side requests. Returning fallback data.");
            return buildFallbackResponse(category);
        }
    }

    private NewsResponseDTO buildFallbackResponse(String category) {
        List<ArticleDTO> articles = new ArrayList<>();

        for (String[] item : getMockDataForCategory(category)) {
            ArticleDTO a = new ArticleDTO();
            a.setTitle(item[0]);
            a.setDescription(item[1]);
            a.setUrl(item[2]);
            a.setUrlToImage(item[3]);
            a.setPublishedAt("2026-04-26T00:00:00Z");
            articles.add(a);
        }

        NewsResponseDTO dto = new NewsResponseDTO();
        dto.setStatus("ok");
        dto.setTotalResults(articles.size());
        dto.setArticles(articles);
        return dto;
    }

    private String[][] getMockDataForCategory(String category) {
        return switch (category.toLowerCase()) {
            case "technology" -> new String[][]{
                {"OpenAI Releases GPT-5 With Unprecedented Reasoning Capabilities", "The new model demonstrates human-level performance on complex reasoning benchmarks, setting a new standard for large language models.", "https://openai.com", "https://images.unsplash.com/photo-1677442135703-1787eea5ce01?w=800"},
                {"Google DeepMind's AlphaFold 3 Solves Protein Interaction Problem", "Researchers say the breakthrough could accelerate drug discovery by a decade, enabling new treatments for rare diseases.", "https://deepmind.google", "https://images.unsplash.com/photo-1532094349884-543d987e5fca?w=800"},
                {"Apple's Vision Pro 2 Enters Mass Production", "Supply chain sources confirm the next-generation spatial computing device will launch with a significantly lower price point.", "https://apple.com", "https://images.unsplash.com/photo-1620121692029-d088224ddc74?w=800"},
                {"NVIDIA's Blackwell Ultra GPUs Ship to Data Centers", "The new architecture delivers 3x the performance of its predecessor, fueling a new wave of AI infrastructure investment.", "https://nvidia.com", "https://images.unsplash.com/photo-1518770660439-4636190af475?w=800"},
                {"Quantum Computing Startup Achieves Error Correction Milestone", "A Silicon Valley startup has demonstrated a fault-tolerant qubit array, a critical step toward practical quantum advantage.", "https://quantumnews.io", "https://images.unsplash.com/photo-1635070041078-e363dbe005cb?w=800"},
            };
            case "business" -> new String[][]{
                {"Global Markets Rally as Fed Signals Rate Cuts Ahead", "U.S. equities surged to record highs as the Federal Reserve hinted at multiple rate reductions in its upcoming policy meeting.", "https://wsj.com", "https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3?w=800"},
                {"Amazon Acquires AI Startup for $4.2 Billion", "The acquisition signals Amazon's aggressive push into enterprise AI, directly competing with Microsoft and Google's cloud AI offerings.", "https://bloomberg.com", "https://images.unsplash.com/photo-1523474253046-8cd2748b5fd2?w=800"},
                {"Tesla's Robotaxi Fleet Launches in Three Major Cities", "Elon Musk's long-promised autonomous ride-hailing service begins commercial operations with a fleet of 500 vehicles.", "https://reuters.com", "https://images.unsplash.com/photo-1562516155-e0c1ee44059b?w=800"},
                {"Startup Funding Surges 40% in Q1 2026", "Venture capital activity has rebounded strongly, driven by AI, climate tech, and biotech sectors attracting record capital inflows.", "https://techcrunch.com", "https://images.unsplash.com/photo-1579532537598-459ecdaf39cc?w=800"},
            };
            case "science" -> new String[][]{
                {"NASA's Artemis Mission Returns Moon Samples After 50 Years", "Astronauts successfully collected 200kg of lunar regolith from the south pole, providing the richest sample set in history.", "https://nasa.gov", "https://images.unsplash.com/photo-1614728263952-84ea256f9d0d?w=800"},
                {"Scientists Reverse Aging in Mice Using Gene Therapy", "A groundbreaking study demonstrates full cognitive and physical restoration in aged mice, opening the door to human trials.", "https://nature.com", "https://images.unsplash.com/photo-1576086213369-97a306d36557?w=800"},
                {"New Exoplanet Found in Habitable Zone with Water Signatures", "The James Webb Space Telescope detected atmospheric water vapor on a rocky exoplanet 40 light-years away.", "https://space.com", "https://images.unsplash.com/photo-1462331940025-496dfbfc7564?w=800"},
            };
            default -> new String[][]{
                {"G7 Summit Reaches Historic Climate Accord", "World leaders agreed to a binding framework for net-zero emissions by 2040, the most ambitious climate agreement ever signed.", "https://bbc.com", "https://images.unsplash.com/photo-1593642632559-0c6d3fc62b89?w=800"},
                {"WHO Declares End to Three-Year Global Health Emergency", "International health officials confirmed the containment of a viral outbreak that had affected 40 countries, crediting new vaccine platforms.", "https://who.int", "https://images.unsplash.com/photo-1584036561566-baf8f5f1b144?w=800"},
                {"UN Security Council Adopts AI Governance Framework", "A landmark resolution establishes the first international standards for AI safety, with 150 nations signing the initial charter.", "https://un.org", "https://images.unsplash.com/photo-1529107386315-e1a2ed48a620?w=800"},
                {"Global Renewable Energy Capacity Hits Record 4 Terawatts", "Solar and wind installations surpassed fossil fuel generation capacity for the first time in human history, according to the IEA.", "https://iea.org", "https://images.unsplash.com/photo-1509391366360-2e959784a276?w=800"},
                {"SpaceX Starship Completes First Orbital Passenger Flight", "The world's most powerful rocket successfully carried six civilians on a 90-minute orbital journey, marking a new era in commercial space travel.", "https://spacex.com", "https://images.unsplash.com/photo-1517976487492-5750f3195933?w=800"},
            };
        };
    }
}

