import numpy as np
from dataclasses import dataclass
from typing import List, Dict, Tuple
import cv2


@dataclass
class DamageAssessment:
    damage_type: str
    severity: str
    area: float  # in square feet
    confidence: float


class BuildingDamageEstimator:
    def __init__(self):
        self.cost_metrics = {
            'Stage1': {
                'window': (30, 70),  # per unit
                'Damage roof': (2, 5),  # per sq ft
                'Damage': (30, 50),  # per square feet
            },
            'Stage2': {
                'window': (50, 100),  # multiple units
                'Damage roof': (500, 1000),  # full section
                'Damage': (40, 70)
            },
            'Stage3': {
                'window': (70, 140),  # multiple units
                'Damage roof': (100, 175),  # full section
                'Damage': (40, 70)
            }
        }

    def classify_damage_severity(self, damage_area: float) -> str:
        """Classify damage severity based on area and detection confidence"""
        if damage_area == -1:
            return 'inconclusive'
       
       
        if damage_area < 10: 
            return 'Stage1'
        elif damage_area < 50: 
            return 'Stage2'
        else:
            return 'Stage3'

    def estimate_repair_cost(self, damages: List[DamageAssessment]) -> Dict[str, Tuple[float, float]]:
        """Calculate min and max repair costs for detected damages"""
        total_costs = {'Stage1': [0, 0], 'Stage2': [0, 0], 'Stage3': [0, 0]}

        for damage in damages:
            #severity = self.classify_damage_severity(damage.area)

            severity = damage.severity

            if damage.damage_type in self.cost_metrics[severity]:
                min_cost, max_cost = self.cost_metrics[severity][damage.damage_type]

                # Apply area multiplication for per-square-foot costs
                min_cost *= damage.area
                max_cost *= damage.area

                total_costs[severity][0] += min_cost
                total_costs[severity][1] += max_cost

        return {k: tuple(v) for k, v in total_costs.items()}


def process_yolo_results(yolo_results, image_size):
    """
    Process YOLO detection results into damage assessments
    Expected format: list of [x1, y1, x2, y2, confidence, class_id]
    """
    damages = []

    # Mapping of YOLO class IDs to damage types (example)
    damage_types = {
        0: 'Broken Window',
        1: 'Building',
        2: 'Damage',
        3: 'Damaged roof',
        4: 'Other',
        5: 'Roof'
    }

    image_area = image_size[0] * image_size[1]

    for yr in yolo_results:
        for box in yr.boxes:
            x1, y1, x2, y2 = box.xyxy[0]  # or box.xywh[0] if using xywh format
            conf = box.conf[0]  # confidence score
            class_id = box.cls[0]  # class id

            # Calculate area in square feet (assuming a conversion factor)
            box_area = (x2 - x1) * (y2 - y1)
            area_percentage = box_area / image_area
            area_sq_ft = area_percentage * 2000  # Assuming 2000 sq ft building

            damage_type = damage_types.get(int(class_id), 'unknown')

            damages.append(DamageAssessment(
                damage_type=damage_type,
                severity='unknown',  # Will be classified by the estimator
                area=area_sq_ft,
                confidence=conf
            ))

    return damages